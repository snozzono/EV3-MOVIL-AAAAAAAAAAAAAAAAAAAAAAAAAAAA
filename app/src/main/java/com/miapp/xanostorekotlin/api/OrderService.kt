package com.miapp.xanostorekotlin.api

import com.miapp.xanostorekotlin.model.Order
import com.miapp.xanostorekotlin.model.OrderItem
import com.miapp.xanostorekotlin.model.Shipping
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {
    // order
    @GET("order") suspend fun getOrders(): List<Order>
    @GET("order/{order_id}") suspend fun getOrder(@Path("order_id") id: Int): Order
    @POST("order") suspend fun createOrder(@Body body: CreateOrderRequest): Order
    @PATCH("order/{order_id}") suspend fun updateOrder(@Path("order_id") id: Int, @Body body: UpdateOrderRequest): Order
    @DELETE("order/{order_id}") suspend fun deleteOrder(@Path("order_id") id: Int)

    // order_item
    @GET("order_item") suspend fun getOrderItems(): List<OrderItem>
    @GET("order_item/{order_item_id}") suspend fun getOrderItem(@Path("order_item_id") id: Int): OrderItem
    @POST("order_item") suspend fun addOrderItem(@Body body: CreateOrderItemRequest): OrderItem
    @PATCH("order_item/{order_item_id}") suspend fun updateOrderItem(@Path("order_item_id") id: Int, @Body body: UpdateOrderItemRequest): OrderItem
    @DELETE("order_item/{order_item_id}") suspend fun deleteOrderItem(@Path("order_item_id") id: Int)

    // shipping
    @GET("shipping") suspend fun getShippings(): List<Shipping>
    @GET("shipping/{shipping_id}") suspend fun getShipping(@Path("shipping_id") id: Int): Shipping
    @POST("shipping") suspend fun createShipping(@Body body: CreateShippingRequest): Shipping
    @PATCH("shipping/{shipping_id}") suspend fun updateShipping(@Path("shipping_id") id: Int, @Body body: UpdateShippingRequest): Shipping
    @DELETE("shipping/{shipping_id}") suspend fun deleteShipping(@Path("shipping_id") id: Int)
}

data class CreateOrderRequest(
    val user_id: Int,
    val total: Double?,
    val status: String?
)

data class UpdateOrderRequest(
    val total: Double?,
    val status: String?
)

data class CreateOrderItemRequest(
    val order_id: Int,
    val product_id: Int,
    val quantity: Int,
    val price: Double?
)

data class UpdateOrderItemRequest(
    val quantity: Int?,
    val price: Double?
)

data class CreateShippingRequest(
    val order_id: Int,
    val address: String,
    val shipping_date: String?,
    val status: String?
)

data class UpdateShippingRequest(
    val address: String?,
    val shipping_date: String?,
    val status: String?
)