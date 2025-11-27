package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.*
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.presentation.adapter.ProductoAdminAdapter
import com.example.doloresapp.utils.Constants
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class GestionProductosActivity : AppCompatActivity() {

    private lateinit var rvProductos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var etBuscar: TextInputEditText
    private lateinit var fabAgregar: ExtendedFloatingActionButton

    private lateinit var adapter: ProductoAdminAdapter
    private lateinit var apiService: ProductoAdminApiService
    
    private var todosProductos: List<ProductoDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_productos)

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()

        apiService = NetworkClient.createService(ProductoAdminApiService::class.java)
        cargarProductos()
    }

    private fun initViews() {
        rvProductos = findViewById(R.id.rvProductos)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        etBuscar = findViewById(R.id.etBuscar)
        fabAgregar = findViewById(R.id.fabAgregar)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = ProductoAdminAdapter(
            productos = emptyList(),
            onEditar = { mostrarDialogEditar(it) },
            onEliminar = { confirmarEliminar(it) },
            onVerQR = { mostrarQR(it) }
        )
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = adapter
    }

    private fun setupSearch() {
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrarProductos(s.toString())
            }
        })
    }

    private fun setupFab() {
        fabAgregar.setOnClickListener {
            mostrarDialogCrear()
        }
    }

    private fun cargarProductos() {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = apiService.getAllProductos()
                if (response.isSuccessful && response.body() != null) {
                    todosProductos = response.body()!!
                    adapter.updateList(todosProductos)
                    updateEmptyState()
                }
            } catch (e: Exception) {
                Toast.makeText(this@GestionProductosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun filtrarProductos(query: String) {
        val filtrados = if (query.isEmpty()) {
            todosProductos
        } else {
            todosProductos.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                it.codigoBarras?.contains(query, ignoreCase = true) == true
            }
        }
        adapter.updateList(filtrados)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        rvProductos.visibility = if (adapter.itemCount == 0) View.GONE else View.VISIBLE
    }


    private fun mostrarDialogCrear() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_producto, null)
        dialogView.findViewById<View>(R.id.cardQR).visibility = View.GONE

        AlertDialog.Builder(this)
            .setTitle("Nuevo Producto")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                crearProducto(dialogView)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogEditar(producto: ProductoDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_producto, null)
        
        // Llenar campos
        dialogView.findViewById<TextInputEditText>(R.id.etNombre).setText(producto.nombre)
        dialogView.findViewById<TextInputEditText>(R.id.etDescripcion).setText(producto.descripcion)
        dialogView.findViewById<TextInputEditText>(R.id.etPrecio).setText(producto.precio.toString())
        dialogView.findViewById<TextInputEditText>(R.id.etStock).setText(producto.stock.toString())
        dialogView.findViewById<TextInputEditText>(R.id.etCodigoBarras).setText(producto.codigoBarras)
        dialogView.findViewById<TextInputEditText>(R.id.etPrincipioActivo).setText(producto.principioActivo)
        dialogView.findViewById<TextInputEditText>(R.id.etConcentracion).setText(producto.concentracion)
        dialogView.findViewById<SwitchMaterial>(R.id.switchReceta).isChecked = producto.requerireReceta == true

        // Mostrar QR si existe
        val cardQR = dialogView.findViewById<View>(R.id.cardQR)
        val imgQR = dialogView.findViewById<ImageView>(R.id.imgQR)
        if (!producto.qrImageUrl.isNullOrEmpty()) {
            cardQR.visibility = View.VISIBLE
            val fullUrl = if (producto.qrImageUrl.startsWith("http")) producto.qrImageUrl
                else Constants.BASE_URL.trimEnd('/') + producto.qrImageUrl
            Glide.with(this).load(fullUrl).into(imgQR)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                actualizarProducto(producto.id, dialogView)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearProducto(dialogView: View) {
        val nombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre).text.toString()
        val descripcion = dialogView.findViewById<TextInputEditText>(R.id.etDescripcion).text.toString()
        val precio = dialogView.findViewById<TextInputEditText>(R.id.etPrecio).text.toString().toDoubleOrNull() ?: 0.0
        val stock = dialogView.findViewById<TextInputEditText>(R.id.etStock).text.toString().toIntOrNull() ?: 0
        val codigoBarras = dialogView.findViewById<TextInputEditText>(R.id.etCodigoBarras).text.toString()
        val principioActivo = dialogView.findViewById<TextInputEditText>(R.id.etPrincipioActivo).text.toString()
        val concentracion = dialogView.findViewById<TextInputEditText>(R.id.etConcentracion).text.toString()
        val requiereReceta = dialogView.findViewById<SwitchMaterial>(R.id.switchReceta).isChecked

        if (nombre.isBlank()) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val request = CrearProductoRequest(
                    nombre = nombre,
                    descripcion = descripcion.ifBlank { null },
                    precio = precio,
                    stock = stock,
                    codigoBarras = codigoBarras.ifBlank { null },
                    principioActivo = principioActivo.ifBlank { null },
                    concentracion = concentracion.ifBlank { null },
                    requerireReceta = requiereReceta
                )

                val response = apiService.crearProducto(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@GestionProductosActivity, 
                        "✅ Producto creado con QR", Toast.LENGTH_SHORT).show()
                    cargarProductos()
                } else {
                    Toast.makeText(this@GestionProductosActivity, 
                        "Error al crear producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@GestionProductosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun actualizarProducto(id: Long, dialogView: View) {
        val nombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre).text.toString()
        val descripcion = dialogView.findViewById<TextInputEditText>(R.id.etDescripcion).text.toString()
        val precio = dialogView.findViewById<TextInputEditText>(R.id.etPrecio).text.toString().toDoubleOrNull() ?: 0.0
        val stock = dialogView.findViewById<TextInputEditText>(R.id.etStock).text.toString().toIntOrNull() ?: 0
        val codigoBarras = dialogView.findViewById<TextInputEditText>(R.id.etCodigoBarras).text.toString()
        val principioActivo = dialogView.findViewById<TextInputEditText>(R.id.etPrincipioActivo).text.toString()
        val concentracion = dialogView.findViewById<TextInputEditText>(R.id.etConcentracion).text.toString()
        val requiereReceta = dialogView.findViewById<SwitchMaterial>(R.id.switchReceta).isChecked

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val request = ActualizarProductoRequest(
                    idProductos = id,
                    nombre = nombre,
                    descripcion = descripcion.ifBlank { null },
                    precio = precio,
                    stock = stock,
                    codigoBarras = codigoBarras.ifBlank { null },
                    principioActivo = principioActivo.ifBlank { null },
                    concentracion = concentracion.ifBlank { null },
                    requerireReceta = requiereReceta
                )

                val response = apiService.actualizarProducto(id, request)
                if (response.isSuccessful) {
                    Toast.makeText(this@GestionProductosActivity, 
                        "✅ Producto actualizado", Toast.LENGTH_SHORT).show()
                    cargarProductos()
                } else {
                    Toast.makeText(this@GestionProductosActivity, 
                        "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@GestionProductosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun confirmarEliminar(producto: ProductoDTO) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de eliminar '${producto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(producto.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(id: Long) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = apiService.eliminarProducto(id)
                if (response.isSuccessful) {
                    Toast.makeText(this@GestionProductosActivity, 
                        "✅ Producto eliminado", Toast.LENGTH_SHORT).show()
                    cargarProductos()
                } else {
                    Toast.makeText(this@GestionProductosActivity, 
                        "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@GestionProductosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun mostrarQR(producto: ProductoDTO) {
        if (producto.qrImageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Este producto no tiene QR", Toast.LENGTH_SHORT).show()
            return
        }

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(500, 500)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setPadding(32, 32, 32, 32)
        }

        val fullUrl = if (producto.qrImageUrl.startsWith("http")) producto.qrImageUrl
            else Constants.BASE_URL.trimEnd('/') + producto.qrImageUrl
        Glide.with(this).load(fullUrl).into(imageView)

        AlertDialog.Builder(this)
            .setTitle("QR: ${producto.nombre}")
            .setView(imageView)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}