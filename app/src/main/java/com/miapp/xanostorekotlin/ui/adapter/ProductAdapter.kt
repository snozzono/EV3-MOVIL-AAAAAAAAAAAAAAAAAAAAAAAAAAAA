package com.miapp.xanostorekotlin.ui.adapter // Paquete del adaptador de RecyclerView

import android.content.Intent
import android.view.LayoutInflater // Import para inflar layouts
import android.view.ViewGroup // Import del contenedor padre en RecyclerView
import androidx.recyclerview.widget.RecyclerView // Import de la clase base RecyclerView
import com.miapp.xanostorekotlin.model.Product // Import del modelo Product (que ahora tiene la lista de imágenes)
import com.miapp.xanostorekotlin.databinding.ItemProductBinding // Import del ViewBinding del item_product.xml
import coil.load // Extensión de Coil para cargar imágenes en ImageView
import com.miapp.xanostorekotlin.ui.ProductDetailActivity

/**
 * ProductAdapter
 * Adaptador para mostrar productos en un RecyclerView.
 * ACTUALIZADO para usar la nueva estructura del modelo de datos de Product.
 */
class ProductAdapter(
    private var items: List<Product> = emptyList(),
    private val onAddToCart: (Product) -> Unit = {}
) : // Adaptador que recibe lista de productos y callback de agregar al carrito
    RecyclerView.Adapter<ProductAdapter.VH>() { // Especificamos el ViewHolder interno

    // ViewHolder interno que contiene una referencia al ViewBinding de un item.
    class VH(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    // Este métodoo se llama cuando el RecyclerView necesita crear un nuevo ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // Obtenemos el 'inflater' del contexto del ViewGroup padre.
        val inflater = LayoutInflater.from(parent.context)
        // Inflamos el layout de nuestro item (item_product.xml) usando ViewBinding.
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        // Creamos y devolvemos una nueva instancia de nuestro ViewHolder.
        return VH(binding)
    }

    // Este métodoo es llamado por el RecyclerView para mostrar los datos en la posición especificada.
    override fun onBindViewHolder(holder: VH, position: Int) {
        // 1. OBTENER EL DATO
        // Obtenemos el objeto 'Product' correspondiente a esta posición en la lista.
        val product = items[position]

        // 2. IMAGEN CON FALLBACK A PATH
        val imageUrl = com.miapp.xanostorekotlin.util.ImageUrlResolver.firstImageUrl(product.images)
        if (!imageUrl.isNullOrBlank()) {
            android.util.Log.d("ProductAdapter", "Cargando imagen desde: $imageUrl")
            holder.binding.imgProduct.load(imageUrl) {
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_dialog_alert)
            }
        } else {
            holder.binding.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // MANEJO DEL CLIC
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_EXTRA", product)
            context.startActivity(intent)
        }

        // 3. ASIGNACIÓN DE TEXTOS (Lógica que ya funcionaba)
        // Asignamos el nombre del producto al TextView 'tvTitle'.
        holder.binding.tvTitle.text = product.name

        // El campo 'description' puede ser nulo. Si es nulo, usamos el operador Elvis (?:) para asignar un string vacío "".
        holder.binding.tvDescription.text = product.description ?: ""

        // El campo 'price' también puede ser nulo. Usamos 'let' para formatear el texto solo si el precio no es nulo.
        // Si no es nulo, formateamos con recursos. Si es nulo, usamos el operador Elvis (?:) para asignar "".
        holder.binding.tvPrice.text = product.price?.let {
            holder.binding.root.context.getString(com.miapp.xanostorekotlin.R.string.price_prefix, it.toString())
        } ?: ""

        // Botón agregar al carrito
        holder.binding.btnAddCart.setOnClickListener {
            // Feedback visual: tintar el ícono de agregar por 0.3s
            val btn = holder.binding.btnAddCart
            val originalColorFilter = btn.colorFilter
            val highlightColor = android.graphics.Color.parseColor("#4CAF50") // verde
            btn.setColorFilter(highlightColor, android.graphics.PorterDuff.Mode.SRC_IN)
            btn.postDelayed({
                // Restaurar color original
                btn.colorFilter = originalColorFilter
            }, 300)

            onAddToCart(product)
        }
    }

    // Devuelve el número total de items en la lista de datos.
    override fun getItemCount(): Int = items.size

    // Métodoo público para actualizar la lista de productos del adaptador desde fuera (ej. desde la Activity/Fragment).
    fun updateData(newItems: List<Product>) {
        // Reemplazamos la lista de items interna con la nueva lista.
        items = newItems
        // Notificamos al RecyclerView que todos los datos han cambiado para que se redibuje.
        // Nota: Para mejor rendimiento en listas grandes, se usaría DiffUtil en lugar de notifyDataSetChanged().
        notifyDataSetChanged()
    }
}
