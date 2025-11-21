package com.miapp.xanostorekotlin.api

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.miapp.xanostorekotlin.api.CartService
import com.miapp.xanostorekotlin.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CartManager
 * Gestiona la obtención/creación de un carrito para el usuario actual y
 * persiste el `cart_id` en SharedPreferences para reutilizarlo.
 */
class CartManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getCartId(): Int? = if (prefs.contains(KEY_CART_ID)) {
        prefs.getInt(KEY_CART_ID, -1).takeIf { it >= 0 }
    } else null

    private fun setCartId(id: Int) {
        prefs.edit { putInt(KEY_CART_ID, id) }
    }

    /**
     * Asegura que exista un `cart_id` para el usuario actual.
     * Si no existe, intenta crearlo llamando al API.
     */
    suspend fun ensureCartId(context: Context, userId: Int): Int {
        val existing = getCartId()
        if (existing != null) return existing

        val service = RetrofitClient.createCartService(context)
        val created = withContext(Dispatchers.IO) {
            service.createCart(CreateCartRequest(user_id = userId))
        }
        setCartId(created.id)
        return created.id
    }

    companion object {
        private const val PREFS_NAME = "cart"
        private const val KEY_CART_ID = "current_cart_id"
    }
}