package com.miapp.xanostorekotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.miapp.xanostorekotlin.api.ProductService
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.databinding.FragmentProductsAdminBinding
import com.miapp.xanostorekotlin.model.Product
import com.miapp.xanostorekotlin.ui.adapter.ProductAdminAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsAdminFragment : Fragment() {

    private var _binding: FragmentProductsAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdminAdapter
    private lateinit var service: ProductService
    private var products: List<Product> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductsAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        service = RetrofitClient.createProductService(requireContext())
        setupRecycler()
        setupFab()
        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        // Refrescar listado al volver desde crear/editar
        loadProducts()
    }

    private fun setupRecycler() {
        adapter = ProductAdminAdapter(
            onToggle = { product -> toggleImmediate(product) },
            onDelete = { product -> confirmDelete(product) },
            onOpenDetail = { product -> openDetail(product) }
        )
        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.miapp.xanostorekotlin.R.id.fragment_container, AddProductFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val list = withContext(Dispatchers.IO) { service.getProducts() }
                products = list
                adapter.submitList(list)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Error cargando productos", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleImmediate(product: Product) {
        val newEnabled = !(product.enabled ?: false)
        updateProductEnabled(product.id, newEnabled)
    }

    private fun updateProductEnabled(productId: Int, enabled: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val updated = withContext(Dispatchers.IO) { service.updateProduct(productId, com.miapp.xanostorekotlin.api.UpdateProductRequest(enabled = enabled)) }
                // Actualizar lista local y UI sin recargar todo
                products = products.map { if (it.id == productId) it.copy(enabled = updated.enabled) else it }
                adapter.submitList(products)
                Snackbar.make(binding.root, if (enabled) "Producto activado" else "Producto desactivado", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "No se pudo actualizar", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDelete(product: Product) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(com.miapp.xanostorekotlin.R.string.confirm_delete_title)
            .setMessage(com.miapp.xanostorekotlin.R.string.confirm_delete_msg)
            .setPositiveButton(com.miapp.xanostorekotlin.R.string.accept) { _, _ -> deleteProduct(product.id) }
            .setNegativeButton(com.miapp.xanostorekotlin.R.string.cancel, null)
            .show()
    }

    private fun deleteProduct(productId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { service.deleteProduct(productId) }
                Snackbar.make(binding.root, "Producto eliminado", Snackbar.LENGTH_SHORT).show()
                loadProducts()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "No se pudo eliminar", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun openDetail(product: Product) {
        // Abre el detalle con opción de editar
        val fragment = AddProductFragment().apply {
            arguments = Bundle().apply { putInt("product_id", product.id) }
        }
        parentFragmentManager.beginTransaction()
            .replace(com.miapp.xanostorekotlin.R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}