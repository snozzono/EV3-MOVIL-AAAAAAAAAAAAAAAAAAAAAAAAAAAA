package com.miapp.xanostorekotlin.api

data class CreateUserRequest(
    val name: String,
    val email: String,
    val role: String,
    val status: Boolean? = null,
    val phone: String? = null,
    val shipping_address: String? = null,
    val password: String? = null
)