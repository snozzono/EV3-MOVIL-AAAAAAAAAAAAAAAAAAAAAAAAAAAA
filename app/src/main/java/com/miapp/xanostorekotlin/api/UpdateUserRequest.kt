package com.miapp.xanostorekotlin.api

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val status: Boolean? = null,
    val phone: String? = null,
    val shipping_address: String? = null,
    val password: String? = null
)

