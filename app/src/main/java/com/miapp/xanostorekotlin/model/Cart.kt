package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("user_id") val userId: Int
)

data class CartItem(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("cart_id") val cartId: Int,
    @SerializedName("product_id") val productId: Int
)