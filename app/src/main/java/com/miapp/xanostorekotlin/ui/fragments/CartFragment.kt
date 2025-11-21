package com.miapp.xanostorekotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanostorekotlin.api.CartManager
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.FragmentCartBinding
import com.miapp.xanostorekotlin.ui.adapter.CartAdapter
import com.miapp.xanostorekotlin.api.UpdateCartItemRequest
import com.miapp.xanostorekotlin.model.CartItem
import com.miapp.xanostorekotlin.api.CreateOrderRequest
import com.miapp.xanostorekotlin.api.CreateOrderItemRequest
import com.miapp.xanostorekotlin.api.CreateShippingRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CartAdapter
    private var productNames: Map<Int, String> = emptyMap()
    private var productThumbs: Map<Int, String?> = emptyMap()
    private var isUpdating: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartAdapter(
            onIncrease = { item -> modifyItemQuantity(item, +1) },
            onDecrease = { item -> modifyItemQuantity(item, -1) },
            onDelete = { item -> deleteItem(item) },
            productNameResolver = { id -> productNames[id] },
            productImageResolver = { id -> productThumbs[id] }
        )
        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = adapter

        loadCartItems()

        binding.btnCheckout.setOnClickListener { openCheckoutScreen() }
    }

    private fun openCheckoutScreen() {
        parentFragmentManager
            .beginTransaction()
            .replace(com.miapp.xanostorekotlin.R.id.fragment_container, CheckoutFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun loadCartItems() {
        val ctx = requireContext()
        val tokenManager = TokenManager(ctx)
        val userId = tokenManager.getUserId()
        if (userId == null) {
            android.widget.Toast.makeText(ctx, "Inicia sesión para ver tu carrito", android.widget.Toast.LENGTH_SHORT).show()
            binding.tvEmpty.visibility = View.VISIBLE
            binding.btnCheckout.isEnabled = false
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val cartId = CartManager(ctx).ensureCartId(ctx, userId)
                val cartService = RetrofitClient.createCartService(ctx)
                val items = withContext(Dispatchers.IO) { cartService.getCartItems() }
                val myItems = items.filter { it.cartId == cartId }

                // Cargar nombres de productos para mostrar en el carrito
                val productService = com.miapp.xanostorekotlin.api.RetrofitClient.createProductService(ctx)
                val products = withContext(Dispatchers.IO) { productService.getProducts() }
                productNames = products.associate { it.id to it.name }
                productThumbs = products.associate { prod ->
                    prod.id to com.miapp.xanostorekotlin.util.ImageUrlResolver.firstImageUrl(prod.images)
                }
                adapter.updateData(myItems)
                binding.tvEmpty.visibility = if (myItems.isEmpty()) View.VISIBLE else View.GONE
                binding.btnCheckout.isEnabled = myItems.isNotEmpty()
            } catch (e: Exception) {
                android.widget.Toast.makeText(ctx, "Error cargando carrito", android.widget.Toast.LENGTH_LONG).show()
                binding.tvEmpty.visibility = View.VISIBLE
                binding.btnCheckout.isEnabled = false
            }
        }
    }

    private fun modifyItemQuantity(item: CartItem, delta: Int) {
        if (isUpdating) return
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                isUpdating = true
                val newQty = (item.quantity + delta).coerceAtLeast(1)
                val cartService = RetrofitClient.createCartService(ctx)
                withContext(Dispatchers.IO) {
                    cartService.updateCartItem(item.id, UpdateCartItemRequest(quantity = newQty))
                }
                loadCartItems()
            } catch (e: Exception) {
                android.widget.Toast.makeText(ctx, "No se pudo actualizar cantidad", android.widget.Toast.LENGTH_LONG).show()
            } finally {
                isUpdating = false
            }
        }
    }

    private fun deleteItem(item: CartItem) {
        if (isUpdating) return
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                isUpdating = true
                val cartService = RetrofitClient.createCartService(ctx)
                withContext(Dispatchers.IO) { cartService.deleteCartItem(item.id) }
                loadCartItems()
            } catch (e: Exception) {
                android.widget.Toast.makeText(ctx, "No se pudo eliminar el ítem", android.widget.Toast.LENGTH_LONG).show()
            } finally {
                isUpdating = false
            }
        }
    }

    private fun checkout() {
        val ctx = requireContext()
        val tokenManager = TokenManager(ctx)
        val userId = tokenManager.getUserId()
        if (userId == null) {
            android.widget.Toast.makeText(ctx, "Inicia sesión para pagar", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val cartManager = CartManager(ctx)
                val cartId = cartManager.ensureCartId(ctx, userId)
                val cartService = RetrofitClient.createCartService(ctx)
                val orderService = RetrofitClient.createOrderService(ctx)
                val items = withContext(Dispatchers.IO) { cartService.getCartItems() }.filter { it.cartId == cartId }
                if (items.isEmpty()) {
                    android.widget.Toast.makeText(ctx, "Tu carrito está vacío", android.widget.Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Simulación de pago: creamos orden con estado pendiente y total nulo
                val order = withContext(Dispatchers.IO) {
                    orderService.createOrder(CreateOrderRequest(user_id = userId, total = null, status = "pending"))
                }
                // Creamos items de la orden a partir del carrito (precio nulo en simulación)
                withContext(Dispatchers.IO) {
                    for (ci in items) {
                        orderService.addOrderItem(CreateOrderItemRequest(order_id = order.id, product_id = ci.productId, quantity = ci.quantity, price = null))
                    }
                    // Creamos solicitud de envío con dirección demo
                    orderService.createShipping(CreateShippingRequest(order_id = order.id, address = "Dirección demo", shipping_date = null, status = "requested"))
                }

                // Limpiamos el carrito eliminando los items
                withContext(Dispatchers.IO) {
                    for (ci in items) { cartService.deleteCartItem(ci.id) }
                }

                android.widget.Toast.makeText(ctx, "Pedido creado y envío solicitado", android.widget.Toast.LENGTH_LONG).show()
                loadCartItems()
            } catch (e: Exception) {
                android.widget.Toast.makeText(ctx, "Error en pago simulado", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}