package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.*
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.utils.Constants
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProcesarRecetaActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var ivReceta: ImageView
    private lateinit var tvTextoExtraido: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var btnAgregarProducto: MaterialButton
    private lateinit var rvProductos: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvCantidadProductos: TextView
    private lateinit var etObservaciones: TextInputEditText
    private lateinit var btnEnviarPedido: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var recetaId: Long = 0
    private var direccion: String = ""
    private var telefono: String = ""
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    private val productosSeleccionados = mutableListOf<ProductoSeleccionado>()
    private lateinit var productosAdapter: ProductosSeleccionadosAdapter

    private val recetasApi by lazy {
        NetworkClient.createService(RecetasDigitalesApi::class.java)
    }

    private val productosApi by lazy {
        NetworkClient.createService(ProductoApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_procesar_receta)

        NetworkClient.init(this)
        
        // Obtener datos del intent
        recetaId = intent.getLongExtra("RECETA_ID", 0)
        direccion = intent.getStringExtra("DIRECCION") ?: ""
        telefono = intent.getStringExtra("TELEFONO") ?: ""
        latitud = intent.getDoubleExtra("LATITUD", 0.0)
        longitud = intent.getDoubleExtra("LONGITUD", 0.0)

        initViews()
        setupRecyclerView()
        cargarDatosReceta()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivReceta = findViewById(R.id.ivReceta)
        tvTextoExtraido = findViewById(R.id.tvTextoExtraido)
        tvDireccion = findViewById(R.id.tvDireccion)
        tvTelefono = findViewById(R.id.tvTelefono)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)
        rvProductos = findViewById(R.id.rvProductos)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)
        tvCantidadProductos = findViewById(R.id.tvCantidadProductos)
        etObservaciones = findViewById(R.id.etObservaciones)
        btnEnviarPedido = findViewById(R.id.btnEnviarPedido)
        progressBar = findViewById(R.id.progressBar)

        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = "Receta #$recetaId"

        tvDireccion.text = direccion
        tvTelefono.text = telefono

        btnAgregarProducto.setOnClickListener { mostrarDialogoBuscarProducto() }
        btnEnviarPedido.setOnClickListener { enviarPedido() }
    }

    private fun setupRecyclerView() {
        productosAdapter = ProductosSeleccionadosAdapter(
            productos = productosSeleccionados,
            onCantidadChange = { actualizarTotal() },
            onEliminar = { producto ->
                productosSeleccionados.remove(producto)
                productosAdapter.notifyDataSetChanged()
                actualizarTotal()
            }
        )
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = productosAdapter
    }

    private fun cargarDatosReceta() {
        val textoExtraido = intent.getStringExtra("TEXTO_EXTRAIDO") ?: "Sin texto"
        val imagenUrl = intent.getStringExtra("IMAGEN_URL")

        tvTextoExtraido.text = textoExtraido

        imagenUrl?.let { url ->
            val fullUrl = if (url.startsWith("http")) url else "${Constants.BASE_URL}$url"
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivReceta)
        }
    }

    private fun mostrarDialogoBuscarProducto() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_buscar_producto, null)
        val etBuscar = dialogView.findViewById<EditText>(R.id.etBuscar)
        val rvResultados = dialogView.findViewById<RecyclerView>(R.id.rvResultados)
        val progressDialog = dialogView.findViewById<ProgressBar>(R.id.progressBar)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Buscar Producto")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .create()

        val resultadosAdapter = ProductosBusquedaAdapter { producto ->
            agregarProducto(producto)
            dialog.dismiss()
        }
        rvResultados.layoutManager = LinearLayoutManager(this)
        rvResultados.adapter = resultadosAdapter

        etBuscar.setOnEditorActionListener { _, _, _ ->
            val query = etBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                buscarProductos(query, resultadosAdapter, progressDialog)
            }
            true
        }

        dialog.show()
    }

    private fun buscarProductos(
        query: String,
        adapter: ProductosBusquedaAdapter,
        progress: ProgressBar
    ) {
        progress.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = productosApi.buscarProductos(query)

                withContext(Dispatchers.Main) {
                    progress.visibility = View.GONE
                    adapter.submitList(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progress.visibility = View.GONE
                    Toast.makeText(
                        this@ProcesarRecetaActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun agregarProducto(producto: ProductoDTO) {
        // Verificar si ya existe
        val existente = productosSeleccionados.find { it.id == producto.id }
        if (existente != null) {
            existente.cantidad++
            productosAdapter.notifyDataSetChanged()
        } else {
            productosSeleccionados.add(
                ProductoSeleccionado(
                    id = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = 1
                )
            )
            productosAdapter.notifyItemInserted(productosSeleccionados.size - 1)
        }
        actualizarTotal()
    }

    private fun actualizarTotal() {
        val subtotal = productosSeleccionados.sumOf { it.precio * it.cantidad }
        val totalConDelivery = subtotal + 5.0 // Costo de delivery
        
        tvSubtotal.text = "S/ %.2f".format(subtotal)
        tvTotal.text = "S/ %.2f".format(totalConDelivery)
        
        val cantidadItems = productosSeleccionados.sumOf { it.cantidad }
        tvCantidadProductos.text = "$cantidadItems item${if (cantidadItems != 1) "s" else ""}"
    }

    private fun enviarPedido() {
        if (productosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnEnviarPedido.isEnabled = false

        val request = ProcesarRecetaRequest(
            recetaId = recetaId,
            productos = productosSeleccionados.map {
                ProductoPedidoItem(productoId = it.id, cantidad = it.cantidad)
            },
            observacionesFarmaceutico = etObservaciones.text.toString().trim().ifEmpty { null }
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = recetasApi.procesarYEnviarReceta(request)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnEnviarPedido.isEnabled = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        val pedido = response.body()?.data
                        AlertDialog.Builder(this@ProcesarRecetaActivity)
                            .setTitle("✅ Pedido Creado")
                            .setMessage(
                                "Pedido #${pedido?.numeroPedido} creado exitosamente.\n\n" +
                                "Total: S/ ${pedido?.total}\n" +
                                "Estado: ${pedido?.estado}\n\n" +
                                "Se enviará a: $direccion"
                            )
                            .setPositiveButton("Aceptar") { _, _ ->
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    } else {
                        Toast.makeText(
                            this@ProcesarRecetaActivity,
                            response.body()?.message ?: "Error al crear pedido",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnEnviarPedido.isEnabled = true
                    Toast.makeText(
                        this@ProcesarRecetaActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // ==================== Data Classes ====================

    data class ProductoSeleccionado(
        val id: Long,
        val nombre: String,
        val precio: Double,
        var cantidad: Int
    )

    // ==================== Adapters ====================

    inner class ProductosSeleccionadosAdapter(
        private val productos: MutableList<ProductoSeleccionado>,
        private val onCantidadChange: () -> Unit,
        private val onEliminar: (ProductoSeleccionado) -> Unit
    ) : RecyclerView.Adapter<ProductosSeleccionadosAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_producto_seleccionado, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(productos[position])
        }

        override fun getItemCount() = productos.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
            private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
            private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
            private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
            private val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
            private val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
            private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

            fun bind(producto: ProductoSeleccionado) {
                tvNombre.text = producto.nombre
                tvPrecio.text = "S/ %.2f".format(producto.precio)
                tvCantidad.text = producto.cantidad.toString()
                tvSubtotal.text = "S/ %.2f".format(producto.precio * producto.cantidad)

                btnMenos.setOnClickListener {
                    if (producto.cantidad > 1) {
                        producto.cantidad--
                        notifyItemChanged(adapterPosition)
                        onCantidadChange()
                    }
                }

                btnMas.setOnClickListener {
                    producto.cantidad++
                    notifyItemChanged(adapterPosition)
                    onCantidadChange()
                }

                btnEliminar.setOnClickListener {
                    onEliminar(producto)
                }
            }
        }
    }

    inner class ProductosBusquedaAdapter(
        private val onProductoClick: (ProductoDTO) -> Unit
    ) : RecyclerView.Adapter<ProductosBusquedaAdapter.ViewHolder>() {

        private val productos = mutableListOf<ProductoDTO>()

        fun submitList(list: List<ProductoDTO>) {
            productos.clear()
            productos.addAll(list)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(productos[position])
        }

        override fun getItemCount() = productos.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)

            fun bind(producto: ProductoDTO) {
                text1.text = producto.nombre
                text2.text = "S/ %.2f - Stock: ${producto.stock}".format(producto.precio)
                itemView.setOnClickListener { onProductoClick(producto) }
            }
        }
    }
}
