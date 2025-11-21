package com.miapp.xanostorekotlin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.model.Product
import android.graphics.PorterDuff
import coil.load

class ProductAdminAdapter(
    private val onToggle: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
    private val onOpenDetail: (Product) -> Unit
) : ListAdapter<Product, ProductAdminAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_admin, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgThumb: ImageView = itemView.findViewById(R.id.imgThumbAdmin)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val btnToggle: ImageButton = itemView.findViewById(R.id.btnToggle)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(product: Product) {
            tvName.text = product.name
            tvDescription.text = product.description ?: ""
            itemView.setOnClickListener { onOpenDetail(product) }
            btnToggle.setOnClickListener { onToggle(product) }
            btnDelete.setOnClickListener { onDelete(product) }

            // Color según estado habilitado (verde) o inhabilitado (gris)
            val colorRes = if (product.enabled == true) android.R.color.holo_green_dark else android.R.color.darker_gray
            val color = ContextCompat.getColor(itemView.context, colorRes)
            btnToggle.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            // Cargar miniatura con fallback (url o path)
            val firstUrl = com.miapp.xanostorekotlin.util.ImageUrlResolver.firstImageUrl(product.images)
            if (!firstUrl.isNullOrBlank()) {
                imgThumb.load(firstUrl) {
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_delete)
                }
                imgThumb.visibility = View.VISIBLE
            } else {
                imgThumb.visibility = View.GONE
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
        }
    }
}