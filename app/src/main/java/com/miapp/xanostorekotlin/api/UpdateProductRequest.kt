package com.miapp.xanostorekotlin.api

import com.miapp.xanostorekotlin.model.ProductImage

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Int? = null,
    val enabled: Boolean? = null,
    val images: List<ProductImage>? = null
)