package com.miapp.xanostorekotlin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.model.Order

class OrderAdapter : ListAdapter<Order, OrderAdapter.VH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvId: TextView = view.findViewById(R.id.tvOrderId)
        private val tvStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        private val tvTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        fun bind(order: Order) {
            tvId.text = "#${order.id}"
            tvStatus.text = order.status ?: "pending"
            tvTotal.text = order.total?.toString() ?: "—"
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Order, newItem: Order) = oldItem == newItem
        }
    }
}