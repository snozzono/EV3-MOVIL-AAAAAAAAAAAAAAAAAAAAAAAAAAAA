package com.miapp.xanostorekotlin.ui // Define el paquete al que pertenece esta clase.

import android.os.Build // Importa la clase Build para verificar la versión de Android del dispositivo.
import android.os.Bundle // Importa la clase Bundle, usada para pasar datos entre actividades.
import android.view.MenuItem // Importa la clase MenuItem para identificar los botones de la barra de acción.
import androidx.appcompat.app.AppCompatActivity // Importa la clase base para actividades que usan la barra de acción de compatibilidad.
import com.miapp.xanostorekotlin.databinding.ActivityProductDetailBinding // Importa la clase de ViewBinding generada para nuestro layout.
import com.miapp.xanostorekotlin.model.Product // Importa nuestro modelo de datos 'Product'.
import com.miapp.xanostorekotlin.ui.adapter.ImageSliderAdapter // Importa el adaptador que creamos para el carrusel de imágenes.

/**
 * ProductDetailActivity
 * Esta actividad muestra los detalles completos de un solo producto,
 * incluyendo un carrusel de imágenes y un botón para volver atrás.
 */
class ProductDetailActivity : AppCompatActivity() { // La clase hereda de AppCompatActivity.

    // Declara una variable para el ViewBinding que se inicializará más tarde (lateinit).
    private lateinit var binding: ActivityProductDetailBinding

    // Este métodoo se llama cuando la actividad se crea por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama al métodoo onCreate de la clase padre.
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding y asigna la referencia a nuestra variable 'binding'.
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        // Establece la vista de contenido de la actividad a la vista raíz de nuestro layout inflado.
        setContentView(binding.root)

        // --- HABILITAR EL BOTÓN DE "VOLVER ATRÁS" ---
        // Accede a la barra de acción (ActionBar) de soporte y, si existe (?.), habilita la visualización del botón "home".
        // El botón "home" se muestra como una flecha de retroceso (<-) por defecto.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // --- RECUPERAR EL PRODUCTO DEL INTENT ---
        // Verifica si la versión de Android es TIRAMISU (API 33) o superior.
        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Si es Android 13+, usa la forma moderna y segura de obtener un objeto 'Serializable' del Intent.
            // Se especifica la clave "PRODUCT_EXTRA" y la clase del objeto esperado (Product::class.java).
            intent.getSerializableExtra("PRODUCT_EXTRA", Product::class.java)
        } else {
            // Si es una versión anterior a Android 13, usa el métodoo obsoleto.
            // Se suprime la advertencia de obsolescencia (@Suppress("DEPRECATION")).
            // Se hace un cast seguro (as? Product) al tipo de dato que esperamos.
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("PRODUCT_EXTRA") as? Product
        }

        // Usa un bloque 'let' con el operador de llamada segura (?.), que solo se ejecuta si 'product' no es nulo.
        product?.let {
            // Si el producto se recuperó exitosamente, llama al métodoo setupUI para configurar la pantalla.
            setupUI(it)
        }
    }

    // Este métodoo configura todos los elementos de la interfaz de usuario con los datos del producto.
    private fun setupUI(product: Product) {
        // --- TÍTULO DE LA ACTIVIDAD ---
        // Establece el título de la barra de acción (ActionBar) con el nombre del producto.
        title = product.name

        // --- CONFIGURAR LOS TEXTOS ---
        // Mostramos detalles limitados: nombre, precio y un resumen breve
        binding.tvProductName.text = product.name
        binding.tvProductPrice.text = product.price?.let { "$it" } ?: "Precio no disponible"
        val shortDesc = product.description?.let {
            if (it.length > 120) it.take(117) + "..." else it
        } ?: "Sin descripción"
        binding.tvProductDescription.text = shortDesc
        // Ocultamos el stock para no mostrar todos los detalles
        binding.tvProductStock.visibility = android.view.View.GONE

        // --- CONFIGURAR EL CARRUSEL DE IMÁGENES ---
        // Usa 'let' para ejecutar este bloque solo si la lista de imágenes del producto no es nula.
        product.images?.let { imageList ->
            // Resolver URLs absolutas con fallback (url o path)
            val imageUrls = imageList.mapNotNull { com.miapp.xanostorekotlin.util.ImageUrlResolver.resolve(it) }

            if (imageUrls.isNotEmpty()) {
                val adapter = ImageSliderAdapter(imageUrls)
                binding.imageViewPager.adapter = adapter
            }
        }
    }

    // --- MANEJAR EL CLIC EN EL BOTÓN "VOLVER ATRÁS" ---
    // Este métodoo se llama automáticamente cuando se presiona un botón de la barra de acción.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Comprueba si el ID del item presionado es 'android.R.id.home'.
        // Este es el ID estándar para el botón de la flecha de retroceso (Up button).
        if (item.itemId == android.R.id.home) {
            // Si es el botón de retroceso, finaliza la actividad actual.
            // Esto destruye ProductDetailActivity y regresa a la actividad anterior en la pila (HomeActivity).
            finish()
            // Devuelve 'true' para indicar que hemos manejado el evento de clic con éxito.
            return true
        }
        // Si el botón presionado no es el que nos interesa, pasamos el evento al métodoo de la clase padre para que lo maneje.
        return super.onOptionsItemSelected(item)
    }
}
