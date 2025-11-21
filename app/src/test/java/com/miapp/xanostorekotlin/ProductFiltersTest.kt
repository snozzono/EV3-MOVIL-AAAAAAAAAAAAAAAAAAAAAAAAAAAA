package com.miapp.xanostorekotlin

import com.miapp.xanostorekotlin.model.Product
import com.miapp.xanostorekotlin.model.ProductImage
import com.miapp.xanostorekotlin.util.ProductFilters
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFiltersTest {
    @Test
    fun `filterEnabled returns only enabled products`() {
        val p1 = Product(1, "A", null, 10, 1, "b", "c", emptyList(), true)
        val p2 = Product(2, "B", null, 10, 1, "b", "c", emptyList(), false)
        val p3 = Product(3, "C", null, 10, 1, "b", "c", emptyList(), null)

        val result = ProductFilters.filterEnabled(listOf(p1, p2, p3))
        assertEquals(listOf(p1), result)
    }

    @Test
    fun `CreateProductRequest enabled defaults to true`() {
        val req = com.miapp.xanostorekotlin.model.CreateProductRequest(
            name = "X",
            description = null,
            price = null,
            images = null
        )
        assertEquals(true, req.enabled)
    }
}