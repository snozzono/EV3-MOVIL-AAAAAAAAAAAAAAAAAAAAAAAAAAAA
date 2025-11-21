package com.miapp.xanostorekotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.UserService
import com.miapp.xanostorekotlin.api.CreateUserRequest
import com.miapp.xanostorekotlin.api.UpdateUserRequest
import com.miapp.xanostorekotlin.databinding.FragmentUsersBinding
import com.miapp.xanostorekotlin.model.User
import com.miapp.xanostorekotlin.ui.users.UserDetailFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var service: UserService
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        service = RetrofitClient.createUserService(requireContext())
        setupRecycler()
        setupFab()
        loadUsers()
    }

    private fun setupRecycler() {
        adapter = UsersAdapter(
            onOpenDetail = { user: User -> openDetail(user) },
            onEdit = { user: User -> showEditDialog(user) },
            onDelete = { user: User -> confirmDelete(user) },
            onToggle = { user: User -> toggleStatus(user) }
        )
        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsers.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddUser.setOnClickListener { showCreateDialog() }
    }

    private fun loadUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val users: List<User> = withContext(Dispatchers.IO) { service.getUsers() }
                adapter.submitList(users)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Error cargando usuarios", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun openDetail(user: User) {
        val fragment = UserDetailFragment().apply {
            arguments = Bundle().apply { putInt("user_id", user.id) }
        }
        parentFragmentManager.beginTransaction()
            .replace(com.miapp.xanostorekotlin.R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showCreateDialog() {
        val ctx = requireContext()
        val container = android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 16, 32, 0)
        }
        val etName = android.widget.EditText(ctx).apply { hint = "Nombre" }
        val etEmail = android.widget.EditText(ctx).apply { hint = "Email" }
        val roles = listOf("admin", "user")
        val spRole = android.widget.Spinner(ctx).apply {
            adapter = android.widget.ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, roles)
            setSelection(1) // por defecto 'user'
        }
        val etPassword = android.widget.EditText(ctx).apply {
            hint = "Contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etPasswordConfirm = android.widget.EditText(ctx).apply {
            hint = "Confirmar contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        container.addView(etName)
        container.addView(etEmail)
        container.addView(spRole)
        container.addView(etPassword)
        container.addView(etPasswordConfirm)

        com.google.android.material.dialog.MaterialAlertDialogBuilder(ctx)
            .setTitle("Crear usuario")
            .setView(container)
            .setPositiveButton("Guardar") { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val role = roles[spRole.selectedItemPosition]
                val pass = etPassword.text.toString()
                val pass2 = etPasswordConfirm.text.toString()
                if (name.isBlank() || email.isBlank()) {
                    com.google.android.material.snackbar.Snackbar.make(binding.root, "Nombre y email obligatorios", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (pass.isBlank() || pass2.isBlank()) {
                    com.google.android.material.snackbar.Snackbar.make(binding.root, "Contraseña y confirmación son obligatorias", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (pass != pass2) {
                    com.google.android.material.snackbar.Snackbar.make(binding.root, "Las contraseñas no coinciden", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val req = CreateUserRequest(
                    name = name,
                    email = email,
                    role = role,
                    password = pass
                )
                createUser(req)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDialog(user: User) {
        val ctx = requireContext()
        val container = android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 16, 32, 0)
        }
        val etName = android.widget.EditText(ctx).apply { hint = "Nombre"; setText(user.name) }
        val etEmail = android.widget.EditText(ctx).apply { hint = "Email"; setText(user.email) }
        val roles = listOf("admin", "user")
        val spRole = android.widget.Spinner(ctx).apply {
            adapter = android.widget.ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, roles)
            val idx = roles.indexOf(user.role).takeIf { it >= 0 } ?: 1
            setSelection(idx)
        }
        val etPassword = android.widget.EditText(ctx).apply {
            hint = "Nueva contraseña (opcional)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etPasswordConfirm = android.widget.EditText(ctx).apply {
            hint = "Confirmar contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        container.addView(etName)
        container.addView(etEmail)
        container.addView(spRole)
        container.addView(etPassword)
        container.addView(etPasswordConfirm)

        com.google.android.material.dialog.MaterialAlertDialogBuilder(ctx)
            .setTitle("Editar usuario")
            .setView(container)
            .setPositiveButton("Actualizar") { _, _ ->
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val role = roles[spRole.selectedItemPosition]
                val pass = etPassword.text.toString()
                val pass2 = etPasswordConfirm.text.toString()
                if (pass.isNotBlank() || pass2.isNotBlank()) {
                    if (pass.isBlank() || pass2.isBlank() || pass != pass2) {
                        com.google.android.material.snackbar.Snackbar.make(binding.root, "Las contraseñas no coinciden", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                }
                val req = UpdateUserRequest(
                    name = name,
                    email = email,
                    role = role,
                    password = pass.takeIf { it.isNotBlank() }
                )
                // Usamos PATCH para actualización parcial
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val updated = withContext(kotlinx.coroutines.Dispatchers.IO) { service.patchUser(user.id, req) }
                        val newList = adapter.currentList.map { if (it.id == user.id) updated else it }
                        adapter.submitList(newList)
                        com.google.android.material.snackbar.Snackbar.make(binding.root, "Usuario actualizado", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        com.google.android.material.snackbar.Snackbar.make(binding.root, "No se pudo actualizar", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDelete(user: User) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar usuario")
            .setMessage("¿Seguro que deseas eliminar a ${user.name}?")
            .setPositiveButton("Eliminar") { _, _ -> deleteUser(user.id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createUser(req: CreateUserRequest) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val created = withContext(Dispatchers.IO) { service.createUser(req) }
                val newList = (adapter.currentList + created)
                adapter.submitList(newList)
                Snackbar.make(binding.root, "Usuario creado", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "No se pudo crear", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUser(userId: Int, req: UpdateUserRequest) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val updated = withContext(Dispatchers.IO) { service.updateUser(userId, req) }
                val newList = adapter.currentList.map { if (it.id == userId) updated else it }
                adapter.submitList(newList)
                Snackbar.make(binding.root, "Usuario actualizado", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "No se pudo actualizar", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteUser(userId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { service.deleteUser(userId) }
                val newList = adapter.currentList.filterNot { it.id == userId }
                adapter.submitList(newList)
                Snackbar.make(binding.root, "Usuario eliminado", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "No se pudo eliminar", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleStatus(user: User) {
        val newStatus = !(user.status ?: false)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val updated = withContext(Dispatchers.IO) {
                    service.patchUser(user.id, UpdateUserRequest(status = newStatus))
                }
                val newList = adapter.currentList.map { if (it.id == user.id) updated else it }
                adapter.submitList(newList)
                Snackbar.make(binding.root, if (newStatus) "Usuario habilitado" else "Usuario deshabilitado", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "No se pudo actualizar estado", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Simple adapter
class UsersAdapter(
    private val onOpenDetail: (User) -> Unit,
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit,
    private val onToggle: (User) -> Unit
) : ListAdapter<User, UsersAdapter.VH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(com.miapp.xanostorekotlin.R.layout.item_user, parent, false)
        return VH(v)
    }
    override fun onBindViewHolder(holder: VH, position: Int) { holder.bind(getItem(position)) }
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(com.miapp.xanostorekotlin.R.id.tvName)
        private val tvEmail: TextView = view.findViewById(com.miapp.xanostorekotlin.R.id.tvEmail)
        private val tvRole: TextView = view.findViewById(com.miapp.xanostorekotlin.R.id.tvRole)
        private val btnEdit: TextView = view.findViewById(com.miapp.xanostorekotlin.R.id.btnEdit)
        private val btnDelete: TextView = view.findViewById(com.miapp.xanostorekotlin.R.id.btnDelete)
        private val switchStatus: androidx.appcompat.widget.SwitchCompat = view.findViewById(com.miapp.xanostorekotlin.R.id.switchStatus)
        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val u: User = getItem(pos)
                    onOpenDetail(u)
                }
            }
            btnEdit.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onEdit(getItem(pos))
            }
            btnDelete.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onDelete(getItem(pos))
            }
            switchStatus.setOnCheckedChangeListener { _, _ ->
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onToggle(getItem(pos))
            }
        }
        fun bind(u: User) {
            tvName.text = u.name
            tvEmail.text = u.email
            val roleText = (u.role ?: "user").lowercase()
            tvRole.text = roleText
            // Color distintivo por rol: admin azul, user gris
            val roleColor = if (roleText == "admin") android.graphics.Color.parseColor("#1976D2") else android.graphics.Color.parseColor("#616161")
            tvRole.setTextColor(roleColor)
            // Fondo tipo badge según rol
            tvRole.setBackgroundResource(if (roleText == "admin") com.miapp.xanostorekotlin.R.drawable.bg_role_admin else com.miapp.xanostorekotlin.R.drawable.bg_role_user)
            // Evitar disparar el listener al asignar programáticamente
            switchStatus.setOnCheckedChangeListener(null)
            val checked = (u.status == true)
            switchStatus.isChecked = checked
            switchStatus.text = if (checked) "Activo" else "Inactivo"
            // Restaurar listener
            switchStatus.setOnCheckedChangeListener { _, _ ->
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onToggle(getItem(pos))
            }
        }
    }
    companion object {
        private val DIFF: DiffUtil.ItemCallback<User> = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }
}