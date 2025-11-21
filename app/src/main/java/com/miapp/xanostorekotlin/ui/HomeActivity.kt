package com.miapp.xanostorekotlin.ui // Paquete de la Activity principal de la app

import android.os.Bundle // Import para ciclo de vida de Activity y estado
import androidx.appcompat.app.AppCompatActivity // Import de la Activity base con compatibilidad
import androidx.fragment.app.Fragment // Import de la clase Fragment (para transacciones)
import androidx.core.view.GravityCompat
import com.miapp.xanostorekotlin.api.TokenManager // Import de nuestra clase para gestionar token/usuario
import com.miapp.xanostorekotlin.databinding.ActivityHomeBinding // Import del ViewBinding del layout activity_home.xml
import com.miapp.xanostorekotlin.ui.fragments.AddProductFragment // Import del fragmento para agregar productos
import com.miapp.xanostorekotlin.ui.fragments.ProductsFragment // Import del fragmento que lista productos
import com.miapp.xanostorekotlin.ui.fragments.ProfileFragment // Import del fragmento de perfil
import com.miapp.xanostorekotlin.ui.fragments.ProductsAdminFragment
import com.miapp.xanostorekotlin.ui.fragments.UsersFragment
import android.content.Intent
import com.miapp.xanostorekotlin.ui.MainActivity

/**
 * HomeActivity
 *
 * Explicación:
 * - Muestra un saludo con el nombre del usuario logeado.
 * - Contiene un BottomNavigationView para navegar entre 3 fragments:
 *   Perfil, Productos y Agregar Producto.
 * - No usamos Navigation Component para mantenerlo sencillo; hacemos transacciones manuales.
 */
class HomeActivity : AppCompatActivity() { // Declaramos la Activity Home, que gestiona los fragments

    private lateinit var binding: ActivityHomeBinding // Referencia al ViewBinding para acceder a vistas
    private lateinit var tokenManager: TokenManager // Manejador de token y datos de usuario

    override fun onCreate(savedInstanceState: Bundle?) { // Métodoo de ciclo de vida: se llama al crear la Activity
        super.onCreate(savedInstanceState) // Llamamos a la implementación base
        binding = ActivityHomeBinding.inflate(layoutInflater) // Inflamos el layout a través de ViewBinding
        setContentView(binding.root) // Establecemos la vista raíz del binding como contenido de la Activity

        tokenManager = TokenManager(this) // Inicializamos el TokenManager con el contexto de la Activity
        // Navbar título para Admin
        binding.tvWelcome.text = "Tienda Don pepe"

        // Ocultar botones de perfil y logout en Admin
        binding.btnProfile.visibility = android.view.View.GONE
        binding.btnLogout.visibility = android.view.View.GONE

        // Menú admin: Productos y Usuarios
        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(com.miapp.xanostorekotlin.R.menu.bottom_nav_admin)

        // Cargar inicialmente Productos (admin)
        replaceFragment(ProductsAdminFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.miapp.xanostorekotlin.R.id.nav_products_admin -> replaceFragment(ProductsAdminFragment())
                com.miapp.xanostorekotlin.R.id.nav_users_admin -> replaceFragment(UsersFragment())
            }
            true
        }

        // Botón de menú: abrir sidebar (Drawer)
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configurar menú del sidebar para Admin (simplificado: Perfil y Cerrar sesión)
        binding.navView.menu.clear()
        binding.navView.inflateMenu(com.miapp.xanostorekotlin.R.menu.drawer_admin)
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.miapp.xanostorekotlin.R.id.drawer_profile_admin -> replaceFragment(ProfileFragment())
                com.miapp.xanostorekotlin.R.id.drawer_logout_admin -> {
                    tokenManager.clear()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Toast de bienvenida cada vez que se entra a Home
        val name = tokenManager.getUserName() ?: ""
        android.widget.Toast.makeText(this, "bienvenido denuevo $name", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun replaceFragment(fragment: Fragment) { // Función auxiliar para reemplazar el fragment actual
        supportFragmentManager.beginTransaction() // Iniciamos una transacción de fragmentos
            .replace(binding.fragmentContainer.id, fragment) // Reemplazamos el contenedor con el fragmento dado
            .commit() // Confirmamos la transacción
    }
}