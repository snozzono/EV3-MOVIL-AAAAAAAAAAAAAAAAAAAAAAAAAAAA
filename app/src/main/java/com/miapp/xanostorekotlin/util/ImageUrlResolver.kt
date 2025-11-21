package com.miapp.xanostorekotlin.util

import com.miapp.xanostorekotlin.api.ApiConfig
import com.miapp.xanostorekotlin.model.ProductImage
import java.net.URI

/**
 * Utilidad para resolver una URL absoluta de imagen a partir del objeto ProductImage.
 * Prioriza el campo `url` si viene en el JSON de Xano. Si está ausente, construye
 * una URL usando el host de `storeBaseUrl` y el `path` devuelto por Xano (ej. "/vault/.../image.jpg").
 */
object ImageUrlResolver {
    fun resolve(image: ProductImage?): String? {
        if (image == null) return null
        val direct = image.url
        if (!direct.isNullOrBlank()) return direct

        val path = image.path
        if (path.isBlank()) return null

        // Construir URL absoluta usando el origen (scheme + host) de storeBaseUrl
        return try {
            val base = URI(ApiConfig.storeBaseUrl)
            val origin = URI("${base.scheme}://${base.host}")
            origin.resolve(path).toString()
        } catch (_: Exception) {
            null
        }
    }

    fun firstImageUrl(images: List<ProductImage>?): String? = resolve(images?.firstOrNull())
}