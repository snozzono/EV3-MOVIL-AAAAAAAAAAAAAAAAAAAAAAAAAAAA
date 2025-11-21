package com.miapp.xanostorekotlin.util

import com.miapp.xanostorekotlin.model.Product

/**
 * Utilidades de filtrado para productos.
 */
object ProductFilters {
    /** Devuelve solo productos habilitados (enabled == true). */
    fun filterEnabled(list: List<Product>): List<Product> = list.filter { it.enabled == true }
}