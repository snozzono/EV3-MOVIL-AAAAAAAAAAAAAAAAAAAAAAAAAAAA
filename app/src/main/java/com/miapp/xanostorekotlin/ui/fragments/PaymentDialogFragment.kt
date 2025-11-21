package com.miapp.xanostorekotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanostorekotlin.api.CartManager
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.FragmentPaymentDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentDialogFragment : DialogFragment() {

    private var _binding: FragmentPaymentDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDialogBinding.inflate(inflater, container, false)
        dialog?.setCanceledOnTouchOutside(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener { startProcessing() }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun startProcessing() {
        binding.btnConfirm.isEnabled = false
        binding.btnCancel.isEnabled = false
        binding.progress.visibility = View.VISIBLE
        binding.tvMessage.text = "Procesando..."

        val ctx = requireContext()
        val tokenManager = TokenManager(ctx)
        val userId = tokenManager.getUserId()
        if (userId == null) {
            android.widget.Toast.makeText(ctx, "Inicia sesión para pagar", android.widget.Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
            binding.btnConfirm.isEnabled = true
            binding.btnCancel.isEnabled = true
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
                    binding.progress.visibility = View.GONE
                    binding.btnCancel.isEnabled = true
                    return@launch
                }

                // Crear la orden y envío (simulación sin datos bancarios)
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

                // Mostrar check de éxito
                binding.progress.visibility = View.GONE
                binding.imgCheck.visibility = View.VISIBLE
                binding.tvMessage.text = "Pago confirmado"
                android.widget.Toast.makeText(ctx, "Pedido creado y envío solicitado", android.widget.Toast.LENGTH_LONG).show()

                // Cerrar después de una pequeña pausa y volver atrás
                delay(1200)
                dismiss()
                requireActivity().supportFragmentManager.popBackStack()
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.tvMessage.text = "Error procesando pago"
                android.widget.Toast.makeText(ctx, "Error en pago simulado", android.widget.Toast.LENGTH_LONG).show()
                binding.btnCancel.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}