package com.miapp.xanostorekotlin.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanostorekotlin.databinding.FragmentUserDetailBinding
import com.miapp.xanostorekotlin.model.Order
import com.miapp.xanostorekotlin.api.OrderService
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.ui.adapter.OrderAdapter
import kotlinx.coroutines.launch

class UserDetailFragment : Fragment() {
    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    private var userId: Int? = null
    private val ordersAdapter = OrderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getInt("user_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrders.adapter = ordersAdapter
        loadOrders()
    }

    private fun loadOrders() {
        val uid = userId
        if (uid == null) {
            Toast.makeText(requireContext(), "Usuario inválido", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                val service: OrderService = RetrofitClient.createOrderService(requireContext())
                val all: List<Order> = service.getOrders()
                val userOrders = all.filter { it.userId == uid }
                ordersAdapter.submitList(userOrders)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando pedidos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}