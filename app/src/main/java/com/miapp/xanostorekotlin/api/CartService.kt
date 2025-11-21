package com.miapp.xanostorekotlin.api

import com.miapp.xanostorekotlin.model.Cart
import com.miapp.xanostorekotlin.model.CartItem
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CartService {
    // cart
    @GET("cart") suspend fun getCarts(): List<Cart>
    @GET("cart/{cart_id}") suspend fun getCart(@Path("cart_id") id: Int): Cart
    @POST("cart") suspend fun createCart(@Body body: CreateCartRequest): Cart
    @PATCH("cart/{cart_id}") suspend fun updateCart(@Path("cart_id") id: Int, @Body body: UpdateCartRequest): Cart
    @DELETE("cart/{cart_id}") suspend fun deleteCart(@Path("cart_id") id: Int)

    // cart_item
    @GET("cart_item") suspend fun getCartItems(): List<CartItem>
    @GET("cart_item/{cart_item_id}") suspend fun getCartItem(@Path("cart_item_id") id: Int): CartItem
    @POST("cart_item") suspend fun addCartItem(@Body body: AddCartItemRequest): CartItem
    @PATCH("cart_item/{cart_item_id}") suspend fun updateCartItem(@Path("cart_item_id") id: Int, @Body body: UpdateCartItemRequest): CartItem
    @DELETE("cart_item/{cart_item_id}") suspend fun deleteCartItem(@Path("cart_item_id") id: Int)
}

data class CreateCartRequest(val user_id: Int)
data class UpdateCartRequest(val user_id: Int?)

data class AddCartItemRequest(
    val cart_id: Int,
    val product_id: Int,
    val quantity: Int
)
data class UpdateCartItemRequest(val quantity: Int?)