package com.miapp.xanostorekotlin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanostorekotlin.databinding.ItemCartBinding
import com.miapp.xanostorekotlin.model.CartItem
import coil.load

class CartAdapter(
    private var items: List<CartItem> = emptyList(),
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit,
    private val productNameResolver: (Int) -> String? = { null },
    private val productImageResolver: (Int) -> String? = { null }
) : RecyclerView.Adapter<CartAdapter.VH>() {

    class VH(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val name = productNameResolver(item.productId)
        holder.binding.tvProductId.text = name?.let { it } ?: "Producto ID: ${item.productId}"
        holder.binding.tvQuantity.text = "${item.quantity}"

        // Cargar imagen del producto si está disponible
        val url = productImageResolver(item.productId)
        if (!url.isNullOrBlank()) {
            holder.binding.imgThumb.load(url) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_gallery)
            }
        } else {
            holder.binding.imgThumb.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.binding.btnIncrease.setOnClickListener { onIncrease(item) }
        holder.binding.btnDecrease.setOnClickListener { onDecrease(item) }
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
    }

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}