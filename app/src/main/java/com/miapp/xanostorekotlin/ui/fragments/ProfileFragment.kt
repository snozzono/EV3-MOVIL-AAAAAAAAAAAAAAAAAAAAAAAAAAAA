package com.miapp.xanostorekotlin.ui.fragments // Declaramos el paquete donde vive este fragmento

import android.content.Intent // Import para crear Intents al navegar entre Activities
import android.os.Bundle // Import para manejar el ciclo de vida y estado guardado
import android.view.LayoutInflater // Import para inflar layouts XML
import android.view.View // Import de la clase View
import android.view.ViewGroup // Import para referencia al contenedor padre
import androidx.fragment.app.Fragment // Import de la clase base Fragment
import com.miapp.xanostorekotlin.api.TokenManager // Import de nuestro gestor de tokens/usuario
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.miapp.xanostorekotlin.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.miapp.xanostorekotlin.databinding.FragmentProfileBinding // Import del ViewBinding generado para fragment_profile.xml
import com.miapp.xanostorekotlin.ui.MainActivity // Import de MainActivity para navegar al login tras logout

/**
 * ProfileFragment
 * Muestra los datos básicos del usuario logeado y permite cerrar sesión.
 * Todas las líneas tienen comentarios para fines didácticos.
 */
class ProfileFragment : Fragment() { // Declaramos la clase del fragmento que hereda de Fragment

    private var _binding: FragmentProfileBinding? = null // Referencia mutable al binding (válida entre onCreateView y onDestroyView)
    private val binding get() = _binding!! // Acceso no nulo al binding mientras la vista existe

    override fun onCreateView( // Métodoo para crear/infla la vista del fragmento
        inflater: LayoutInflater, // Inflador para convertir XML en objetos View
        container: ViewGroup?, // Contenedor padre donde se insertará la vista
        savedInstanceState: Bundle? // Estado previamente guardado (no usado aquí)
    ): View { // Retornamos un View
        _binding = FragmentProfileBinding.inflate(inflater, container, false) // Inflamos el layout usando ViewBinding
        return binding.root // Retornamos la raíz del layout inflado
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Métodoo llamado cuando la vista ya fue creada
        super.onViewCreated(view, savedInstanceState) // Llamamos a la superclase
        val tm = TokenManager(requireContext()) // Instanciamos el TokenManager para leer datos del usuario
        binding.etName.setText(tm.getUserName()) // Set inicial
        binding.etEmail.setText(tm.getUserEmail()) // Set inicial
        // Si en el futuro guardamos estos campos en preferencias, los podríamos rellenar aquí
        binding.etPhone.setText("")
        binding.etShippingAddress.setText("")

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim().orEmpty()
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val phone = binding.etPhone.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
            val shipping = binding.etShippingAddress.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
            val password = binding.etPassword.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
            if (name.isBlank() || email.isBlank()) {
                Snackbar.make(binding.root, "Nombre y email son obligatorios", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userId = tm.getUserId()
            val role = tm.getUserRole()
            val token = tm.getToken()
            if (userId == null || role == null || token == null) {
                Snackbar.make(binding.root, "Sesión no válida", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val service = RetrofitClient.createUserService(requireContext())
                    val updated = withContext(Dispatchers.IO) {
                        com.miapp.xanostorekotlin.api.UpdateUserRequest(
                            name = name,
                            email = email,
                            phone = phone,
                            shipping_address = shipping,
                            password = password
                        ).let { req ->
                            service.patchUser(userId, req)
                        }
                    }
                    // Refrescar preferencias locales con nuevos datos
                    tm.saveAuth(token, userId, updated.name ?: name, updated.email ?: email, role)
                    Snackbar.make(binding.root, "Perfil actualizado", Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Error al guardar", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.btnLogout.setOnClickListener { // Asociamos un listener al botón de Cerrar sesión
            tm.clear() // Limpiamos token y datos del usuario de SharedPreferences
            // Creamos un Intent para ir a MainActivity (pantalla de login)
            val intent = Intent(requireContext(), MainActivity::class.java) // Intent explícito hacia MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Limpiamos el back stack para que no se pueda volver con atrás
            startActivity(intent) // Lanzamos la actividad de login
            requireActivity().finish() // Cerramos la HomeActivity actual para completar el logout
        }
    }

    override fun onDestroyView() { // Métodoo llamado cuando la vista del fragmento se está destruyendo
        super.onDestroyView() // Llamamos a la superclase
        _binding = null // Liberamos el binding para evitar fugas de memoria
    }
}