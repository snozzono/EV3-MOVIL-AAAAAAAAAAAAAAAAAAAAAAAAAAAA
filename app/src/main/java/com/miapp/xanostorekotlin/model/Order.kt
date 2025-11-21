package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("total") val total: Double?,
    @SerializedName("status") val status: String?,
    @SerializedName("user_id") val userId: Int
)

data class OrderItem(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double?,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("product_id") val productId: Int
)

data class Shipping(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("address") val address: String,
    @SerializedName("shipping_date") val shippingDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("order_id") val orderId: Int
)