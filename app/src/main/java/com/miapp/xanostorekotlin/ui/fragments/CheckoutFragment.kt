package com.miapp.xanostorekotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanostorekotlin.api.CartManager
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.FragmentCheckoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSummary()
        binding.btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnConfirm.setOnClickListener {
            PaymentDialogFragment().show(parentFragmentManager, "PaymentDialog")
        }
    }

    private fun loadSummary() {
        val ctx = requireContext()
        val tokenManager = TokenManager(ctx)
        val userId = tokenManager.getUserId()
        if (userId == null) {
            android.widget.Toast.makeText(ctx, "Inicia sesión para pagar", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val cartId = CartManager(ctx).ensureCartId(ctx, userId)
                val cartService = RetrofitClient.createCartService(ctx)
                val items = withContext(Dispatchers.IO) { cartService.getCartItems() }.filter { it.cartId == cartId }
                val productService = RetrofitClient.createProductService(ctx)
                val products = withContext(Dispatchers.IO) { productService.getProducts() }
                // Mapear a nombre y precio como Double para cálculos consistentes
                val names = products.associate { it.id to (it.name to (it.price?.toDouble() ?: 0.0)) }
                val summary = items.joinToString("\n") { ci ->
                    val pair = names[ci.productId]
                    val name = pair?.first ?: "Producto ${ci.productId}"
                    val price = pair?.second ?: 0.0
                    "$name x${ci.quantity} — $${price * ci.quantity}"
                }
                val total = items.sumOf { ci -> ((names[ci.productId]?.second ?: 0.0) * ci.quantity.toDouble()) }
                binding.tvSummary.text = summary
                binding.tvTotal.text = "Total estimado: $${String.format("%.2f", total)}"
            } catch (e: Exception) {
                android.widget.Toast.makeText(ctx, "Error preparando resumen", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performCheckout() {
        // Reutilizamos lógica existente de CartFragment.checkout()
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

                val order = withContext(Dispatchers.IO) {
                    orderService.createOrder(com.miapp.xanostorekotlin.api.CreateOrderRequest(user_id = userId, total = null, status = "pending"))
                }
                withContext(Dispatchers.IO) {
                    for (ci in items) {
                        orderService.addOrderItem(com.miapp.xanostorekotlin.api.CreateOrderItemRequest(order_id = order.id, product_id = ci.productId, quantity = ci.quantity, price = null))
                    }
                    orderService.createShipping(com.miapp.xanostorekotlin.api.CreateShippingRequest(order_id = order.id, address = "Dirección demo", shipping_date = null, status = "requested"))
                }

                withContext(Dispatchers.IO) { for (ci in items) cartService.deleteCartItem(ci.id) }

                android.widget.Toast.makeText(ctx, "Pedido creado y envío solicitado", android.widget.Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
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