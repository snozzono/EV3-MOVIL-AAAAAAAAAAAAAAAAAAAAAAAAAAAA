package com.miapp.xanostorekotlin.ui.fragments // Paquete para los fragments de la app

import android.os.Bundle // Import para estado y ciclo de vida de fragment
import android.view.LayoutInflater // Import para inflar layouts
import android.view.View // Import básico de View
import android.view.ViewGroup // Import del contenedor padre
import androidx.appcompat.widget.SearchView // Import de la barra de búsqueda
import androidx.fragment.app.Fragment // Clase base Fragment
import androidx.lifecycle.lifecycleScope // Alcance de corrutinas atado al ciclo de vida
import androidx.recyclerview.widget.LinearLayoutManager // Layout manager lineal para RecyclerView
import com.miapp.xanostorekotlin.api.RetrofitClient // Cliente centralizado de Retrofit
import com.miapp.xanostorekotlin.databinding.FragmentProductsBinding // ViewBinding del layout fragment_products.xml
import com.miapp.xanostorekotlin.model.Product // Modelo de producto
import com.miapp.xanostorekotlin.ui.adapter.ProductAdapter // Adaptador para el RecyclerView
import kotlinx.coroutines.Dispatchers // Dispatcher para IO
import kotlinx.coroutines.launch // Lanzador de corrutinas
import kotlinx.coroutines.withContext // Cambio de contexto en corrutinas

/**
 * ProductsFragment
 *
 * Explicación:
 * - Obtiene la lista de productos desde la API de Xano usando corrutinas.
 * - Muestra los productos en un RecyclerView.
 * - Incluye una barra de búsqueda para buscar por nombre (filtrado local).
 */
class ProductsFragment : Fragment() { // Fragment que lista y filtra productos

    private var _binding: FragmentProductsBinding? = null // Backing field opcional para ViewBinding
    private val binding get() = _binding!! // Exponemos binding no-null dentro del ciclo de vida de la vista

    private lateinit var adapter: ProductAdapter // Adaptador de productos
    private var allProducts: List<Product> = emptyList() // Cache local de todos los productos

    override fun onCreateView( // Inflamos la vista del fragment
        inflater: LayoutInflater, // Inflater para convertir XML en Views
        container: ViewGroup?, // Contenedor padre
        savedInstanceState: Bundle? // Estado guardado
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false) // Inflamos con ViewBinding
        return binding.root // Devolvemos la raíz de la vista
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Vista creada: configuramos UI y carga
        super.onViewCreated(view, savedInstanceState)
        setupRecycler() // Preparamos RecyclerView
        setupSearch() // Configuramos barra de búsqueda
        loadProducts() // Cargamos datos desde API
    }

    override fun onResume() {
        super.onResume()
        // Refrescar lista de productos al volver a la pantalla
        loadProducts()
    }

    private fun setupRecycler() { // Inicializa RecyclerView con layout manager y adaptador
        adapter = ProductAdapter(onAddToCart = { product ->
            addProductToCart(product)
        }) // Instanciamos adaptador con callback de carrito
        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext()) // Lista vertical
        binding.recyclerProducts.adapter = adapter // Asociamos adaptador
    }

    private fun setupSearch() { // Configura callbacks de búsqueda
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener { // Listener de texto
            override fun onQueryTextSubmit(query: String?): Boolean { // Al enviar búsqueda
                filter(query) // Aplicamos filtro
                return true // Indicamos que manejamos el evento
            }

            override fun onQueryTextChange(newText: String?): Boolean { // Mientras cambia el texto
                filter(newText) // Aplicamos filtro en tiempo real
                return true
            }
        })
    }

    private fun filter(query: String?) { // Filtra lista local por nombre
        val q = query?.trim()?.lowercase().orEmpty() // Normalizamos query a minúsculas
        if (q.isBlank()) { // Si vacío, mostramos todos
            adapter.updateData(allProducts) // Reset
        } else {
            adapter.updateData(allProducts.filter { it.name.lowercase().contains(q) }) // Filtro simple
        }
    }

    private fun loadProducts() { // Carga productos desde API con corrutinas
        // Corrutina para carga de productos
        viewLifecycleOwner.lifecycleScope.launch { // Lanzamos en el ciclo de vida del fragment
            try {
                val service = RetrofitClient.createProductService(requireContext()) // Obtenemos servicio de productos
                val products = withContext(Dispatchers.IO) { // Ejecutamos llamada en hilo de IO
                    service.getProducts() // GET /products
                }
                // Solo mostrar productos habilitados al usuario (evitar ítems deshabilitados)
                val visible = com.miapp.xanostorekotlin.util.ProductFilters.filterEnabled(products)
                allProducts = visible // Guardamos lista para futuras interacciones
                adapter.updateData(visible) // Actualizamos la UI con filtrados
            } catch (e: Exception) {
                android.widget.Toast.makeText(requireContext(), "Error cargando productos", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addProductToCart(product: Product) {
        val ctx = requireContext()
        val tokenManager = com.miapp.xanostorekotlin.api.TokenManager(ctx)
        val userId = tokenManager.getUserId()
        if (userId == null) {
            android.widget.Toast.makeText(ctx, "Inicia sesión para agregar al carrito", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val cartManager = com.miapp.xanostorekotlin.api.CartManager(ctx)
                val cartId = cartManager.ensureCartId(ctx, userId)
                val cartService = com.miapp.xanostorekotlin.api.RetrofitClient.createCartService(ctx)
                // Buscar si el producto ya existe en el carrito del usuario
                val existingItem = withContext(Dispatchers.IO) {
                    cartService.getCartItems()
                        .firstOrNull { it.cartId == cartId && it.productId == product.id }
                }

                withContext(Dispatchers.IO) {
                    if (existingItem == null) {
                        // No existe: agregamos nuevo ítem con cantidad 1
                        cartService.addCartItem(
                            com.miapp.xanostorekotlin.api.AddCartItemRequest(
                                cart_id = cartId,
                                product_id = product.id,
                                quantity = 1
                            )
                        )
                    } else {
                        // Ya existe: actualizamos cantidad sumando 1
                        cartService.updateCartItem(
                            existingItem.id,
                            com.miapp.xanostorekotlin.api.UpdateCartItemRequest(quantity = existingItem.quantity + 1)
                        )
                    }
                }
                android.widget.Toast.makeText(ctx, "${product.name} agregado al carrito", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(ctx, "No se pudo agregar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() { // Limpieza de ViewBinding para evitar memory leaks
        super.onDestroyView()
        _binding = null // Nulificamos binding cuando se destruye la vista
    }
}