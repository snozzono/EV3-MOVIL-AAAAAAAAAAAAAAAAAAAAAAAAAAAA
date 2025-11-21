package com.miapp.xanostorekotlin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityHomeBinding
import com.miapp.xanostorekotlin.ui.fragments.ProductsFragment
import com.miapp.xanostorekotlin.ui.fragments.ProfileFragment
import com.miapp.xanostorekotlin.ui.fragments.CartFragment

class HomeClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        binding.tvWelcome.text = getString(com.miapp.xanostorekotlin.R.string.welcome_name, tokenManager.getUserName() ?: "")

        // Ocultar botones del header para clientes
        binding.btnProfile.visibility = android.view.View.GONE
        binding.btnLogout.visibility = android.view.View.GONE

        // Menú inferior específico para cliente: solo Productos y Carrito
        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(com.miapp.xanostorekotlin.R.menu.bottom_nav_client)

        // Cargar productos por defecto
        replaceFragment(ProductsFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_products -> replaceFragment(ProductsFragment())
                R.id.nav_cart -> replaceFragment(CartFragment())
            }
            true
        }

        // Abrir sidebar con botón de menú
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Manejo del NavigationView (sidebar) simplificado: Perfil y Cerrar sesión
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_profile -> replaceFragment(ProfileFragment())
                R.id.drawer_logout -> {
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Confirmar cierre de sesión")
                        .setMessage("¿Seguro que quieres cerrar sesión?")
                        .setPositiveButton("Sí") { _, _ ->
                            tokenManager.clear()
                            startActivity(android.content.Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Header sin botones para cliente

        // Toast de bienvenida cada vez que se entra a Home Cliente
        val name = tokenManager.getUserName() ?: ""
        android.widget.Toast.makeText(this, "bienvenido denuevo $name", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}