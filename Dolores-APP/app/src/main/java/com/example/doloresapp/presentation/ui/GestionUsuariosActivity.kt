package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.doloresapp.R
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.*
import com.example.doloresapp.presentation.adapters.UsuarioConRol
import com.example.doloresapp.presentation.adapters.UsuariosAdminAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyState: View
    private lateinit var progressBar: View
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var adapter: UsuariosAdminAdapter
    private lateinit var api: AdminApi

    private var usuarios: List<UsuarioAdmin> = emptyList()
    private var roles: List<RolResponse> = emptyList()
    private var usuarioRoles: List<UsuarioRolResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        // Inicializar NetworkClient
        TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)

        setupToolbar()
        setupViews()
        setupRecyclerView()

        api = NetworkClient.createService(AdminApi::class.java)
        loadData()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }


    private fun setupViews() {
        recyclerView = findViewById(R.id.recycler_usuarios)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        emptyState = findViewById(R.id.empty_state)
        progressBar = findViewById(R.id.progress_bar)
        fabAgregar = findViewById(R.id.fab_agregar)

        swipeRefresh.setOnRefreshListener { loadData() }
        swipeRefresh.setColorSchemeResources(R.color.dolores)

        fabAgregar.setOnClickListener { mostrarDialogoCrearUsuario() }
    }

    private fun setupRecyclerView() {
        adapter = UsuariosAdminAdapter(
            onAsignarRol = { usuario -> mostrarDialogoAsignarRol(usuario) },
            onEditar = { usuario -> mostrarDialogoEditarUsuario(usuario) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                // Cargar usuarios, roles y asignaciones
                usuarios = try { api.getAllUsuarios() } catch (e: Exception) { emptyList() }
                roles = try { api.getAllRoles() } catch (e: Exception) { emptyList() }
                usuarioRoles = try { api.getAllUsuarioRoles() } catch (e: Exception) { emptyList() }

                // Combinar usuarios con sus roles
                val usuariosConRol = usuarios.map { usuario ->
                    val asignacion = usuarioRoles.find { it.usuarios?.idUsuarios == usuario.idUsuarios }
                    UsuarioConRol(
                        usuario = usuario,
                        rolNombre = asignacion?.roles?.nombre,
                        usuarioRolId = asignacion?.idUsuarioRol
                    )
                }

                adapter.submitList(usuariosConRol)
                emptyState.visibility = if (usuariosConRol.isEmpty()) View.VISIBLE else View.GONE

            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun mostrarDialogoCrearUsuario() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_crear_usuario, null)
        val etUsuario = dialogView.findViewById<TextInputEditText>(R.id.et_usuario)
        val etCorreo = dialogView.findViewById<TextInputEditText>(R.id.et_correo)
        val etPassword = dialogView.findViewById<TextInputEditText>(R.id.et_password)

        AlertDialog.Builder(this)
            .setTitle("Crear Usuario")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val usuario = etUsuario.text?.toString()?.trim() ?: ""
                val correo = etCorreo.text?.toString()?.trim() ?: ""
                val password = etPassword.text?.toString() ?: ""

                if (usuario.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                crearUsuario(usuario, correo, password)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearUsuario(usuario: String, correo: String, password: String) {
        lifecycleScope.launch {
            try {
                val request = CrearUsuarioRequest(
                    usuario = usuario,
                    correo = correo,
                    contrasena = password
                )
                api.crearUsuario(request)
                Toast.makeText(this@GestionUsuariosActivity, "Usuario creado", Toast.LENGTH_SHORT).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun mostrarDialogoAsignarRol(usuarioConRol: UsuarioConRol) {
        if (roles.isEmpty()) {
            Toast.makeText(this, "No hay roles disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val nombresRoles = roles.map { it.nombre ?: "Rol ${it.idRoles}" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Asignar Rol a ${usuarioConRol.usuario.usuario}")
            .setItems(nombresRoles) { _, which ->
                val rolSeleccionado = roles[which]
                asignarRol(usuarioConRol, rolSeleccionado)
            }
            .setNegativeButton("Cancelar", null)
            .apply {
                // Si ya tiene rol, mostrar opción de quitar
                if (usuarioConRol.usuarioRolId != null) {
                    setNeutralButton("Quitar Rol") { _, _ ->
                        quitarRol(usuarioConRol.usuarioRolId)
                    }
                }
            }
            .show()
    }

    private fun asignarRol(usuarioConRol: UsuarioConRol, rol: RolResponse) {
        lifecycleScope.launch {
            try {
                // Si ya tiene un rol, primero lo quitamos
                usuarioConRol.usuarioRolId?.let {
                    try { api.eliminarUsuarioRol(it) } catch (e: Exception) { }
                }

                // Asignar nuevo rol
                val request = UsuarioRolRequest(
                    usuarios = UsuarioRef(usuarioConRol.usuario.idUsuarios!!),
                    roles = RolRef(rol.idRoles)
                )
                api.asignarRol(request)
                Toast.makeText(this@GestionUsuariosActivity, 
                    "Rol ${rol.nombre} asignado", Toast.LENGTH_SHORT).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun quitarRol(usuarioRolId: Long) {
        lifecycleScope.launch {
            try {
                api.eliminarUsuarioRol(usuarioRolId)
                Toast.makeText(this@GestionUsuariosActivity, "Rol eliminado", Toast.LENGTH_SHORT).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoEditarUsuario(usuarioConRol: UsuarioConRol) {
        val usuario = usuarioConRol.usuario
        val estados = arrayOf("ACTIVO", "INACTIVO")
        
        // Opciones según el rol
        val opciones = mutableListOf("Cambiar Estado", "Crear como Repartidor", "Eliminar Usuario")

        AlertDialog.Builder(this)
            .setTitle("Editar ${usuario.usuario}")
            .setItems(opciones.toTypedArray()) { _, which ->
                when (which) {
                    0 -> {
                        AlertDialog.Builder(this)
                            .setTitle("Cambiar Estado")
                            .setItems(estados) { _, estadoIndex ->
                                cambiarEstadoUsuario(usuario, estados[estadoIndex])
                            }
                            .show()
                    }
                    1 -> {
                        mostrarDialogoCrearRepartidor(usuario)
                    }
                    2 -> {
                        AlertDialog.Builder(this)
                            .setTitle("Eliminar Usuario")
                            .setMessage("¿Estás seguro de eliminar a ${usuario.usuario}?")
                            .setPositiveButton("Eliminar") { _, _ ->
                                eliminarUsuario(usuario.idUsuarios!!)
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun mostrarDialogoCrearRepartidor(usuario: UsuarioAdmin) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_crear_repartidor, null)
        val etNombres = dialogView.findViewById<TextInputEditText>(R.id.et_nombres)
        val etApellidos = dialogView.findViewById<TextInputEditText>(R.id.et_apellidos)
        val etDni = dialogView.findViewById<TextInputEditText>(R.id.et_dni)
        val etTelefono = dialogView.findViewById<TextInputEditText>(R.id.et_telefono)
        val etVehiculo = dialogView.findViewById<TextInputEditText>(R.id.et_vehiculo)
        val etPlaca = dialogView.findViewById<TextInputEditText>(R.id.et_placa)

        AlertDialog.Builder(this)
            .setTitle("Crear Repartidor para ${usuario.usuario}")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val nombres = etNombres.text?.toString()?.trim() ?: ""
                val apellidos = etApellidos.text?.toString()?.trim() ?: ""
                val dni = etDni.text?.toString()?.trim() ?: ""
                val telefono = etTelefono.text?.toString()?.trim() ?: ""
                val vehiculo = etVehiculo.text?.toString()?.trim()
                val placa = etPlaca.text?.toString()?.trim()

                if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(this, "Nombres, apellidos, DNI y teléfono son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                crearRepartidor(usuario, nombres, apellidos, dni, telefono, vehiculo, placa)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun crearRepartidor(
        usuario: UsuarioAdmin,
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        vehiculo: String?,
        placa: String?
    ) {
        lifecycleScope.launch {
            try {
                val repartidoresApi = NetworkClient.createService(RepartidoresApi::class.java)
                
                val request = CrearRepartidorRequest(
                    nombres = nombres,
                    apellidos = apellidos,
                    dni = dni,
                    telefono = telefono,
                    vehiculo = vehiculo?.ifEmpty { null },
                    placaVehiculo = placa?.ifEmpty { null },
                    estado = "ACTIVO",
                    Repartidores = RepartidorUsuarioRef(usuario.idUsuarios!!)
                )
                
                repartidoresApi.crearRepartidor(request)
                
                // También asignar el rol REPARTIDOR si no lo tiene
                val rolRepartidor = roles.find { it.nombre?.uppercase() == "REPARTIDOR" }
                if (rolRepartidor != null) {
                    try {
                        val usuarioRolRequest = UsuarioRolRequest(
                            usuarios = UsuarioRef(usuario.idUsuarios),
                            roles = RolRef(rolRepartidor.idRoles)
                        )
                        api.asignarRol(usuarioRolRequest)
                    } catch (e: Exception) {
                        // Ya tiene el rol, ignorar
                    }
                }
                
                Toast.makeText(this@GestionUsuariosActivity, 
                    "✅ Repartidor creado: $nombres $apellidos", Toast.LENGTH_SHORT).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cambiarEstadoUsuario(usuario: UsuarioAdmin, nuevoEstado: String) {
        lifecycleScope.launch {
            try {
                val usuarioActualizado = usuario.copy(estado = nuevoEstado)
                api.actualizarUsuario(usuario.idUsuarios!!, usuarioActualizado)
                Toast.makeText(this@GestionUsuariosActivity, "Estado actualizado", Toast.LENGTH_SHORT).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarUsuario(usuarioId: Long) {
        lifecycleScope.launch {
            try {
                api.eliminarUsuario(usuarioId)
                Toast.makeText(this@GestionUsuariosActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(this@GestionUsuariosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
