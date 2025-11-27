package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.QRApiService
import com.example.doloresapp.data.remote.api.PedidoApiService
import com.example.doloresapp.data.remote.dto.CrearPedidoPresencialRequest
import com.example.doloresapp.data.remote.dto.DetallePedidoRequest
import com.example.doloresapp.data.remote.dto.PedidoDTO
import com.example.doloresapp.di.ServiceLocator
import com.example.doloresapp.domain.model.ItemVenta
import com.example.doloresapp.domain.model.toDomain
import com.example.doloresapp.presentation.adapter.VentaProductoAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

class VentaPresencialActivity : AppCompatActivity() {

    private lateinit var rvProductos: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvCantidadItems: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnEscanear: MaterialButton
    private lateinit var btnConfirmarVenta: MaterialButton
    private lateinit var btnLimpiar: MaterialButton
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: VentaProductoAdapter
    private lateinit var qrApiService: QRApiService
    private lateinit var pedidoApiService: PedidoApiService

    private val itemsVenta = mutableListOf<ItemVenta>()
    private var scannerDialog: AlertDialog? = null

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_presencial)

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupButtons()

        qrApiService = ServiceLocator.provideQRApiService()
        pedidoApiService = NetworkClient.createService(PedidoApiService::class.java)
    }

    private fun initViews() {
        rvProductos = findViewById(R.id.rvProductos)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvCantidadItems = findViewById(R.id.tvCantidadItems)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)
        btnEscanear = findViewById(R.id.btnEscanear)
        btnConfirmarVenta = findViewById(R.id.btnConfirmarVenta)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    private fun setupRecyclerView() {
        adapter = VentaProductoAdapter(itemsVenta) {
            updateUI()
        }
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = adapter
    }

    private fun setupButtons() {
        btnEscanear.setOnClickListener {
            if (checkCameraPermission()) {
                showScannerDialog()
            } else {
                requestCameraPermission()
            }
        }

        btnConfirmarVenta.setOnClickListener {
            confirmarVenta()
        }

        btnLimpiar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Limpiar pedido")
                .setMessage("¿Estás seguro de eliminar todos los productos?")
                .setPositiveButton("Sí") { _, _ ->
                    itemsVenta.clear()
                    adapter.notifyDataSetChanged()
                    updateUI()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showScannerDialog()
            } else {
                Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showScannerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_qr_scanner, null)
        val barcodeView = dialogView.findViewById<DecoratedBarcodeView>(R.id.barcode_scanner)

        scannerDialog = AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            .setView(dialogView)
            .setCancelable(true)
            .setOnDismissListener { barcodeView.pause() }
            .create()

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    barcodeView.pause()
                    scannerDialog?.dismiss()
                    buscarProducto(it.text)
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })

        scannerDialog?.show()
        barcodeView.resume()
    }

    private fun buscarProducto(codigo: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Primero intentar como ID numérico (QR contiene el ID)
                var response = try {
                    val productoId = codigo.toLong()
                    qrApiService.getProductoByQR(productoId)
                } catch (e: NumberFormatException) {
                    null
                }

                // Si no funciona, intentar por código de barras
                if (response == null || !response.isSuccessful) {
                    response = qrApiService.getProductoByCodigoBarras(codigo)
                }

                if (response != null && response.isSuccessful && response.body() != null) {
                    val productoDTO = response.body()!!
                    val producto = productoDTO.toDomain()
                    agregarProducto(producto)
                } else {
                    Toast.makeText(this@VentaPresencialActivity,
                        "Producto no encontrado: $codigo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VentaPresencialActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun agregarProducto(producto: com.example.doloresapp.domain.model.Producto) {
        // Verificar stock disponible
        if (producto.stock <= 0) {
            Toast.makeText(this, "⚠️ ${producto.nombre} sin stock disponible", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Verificar si ya existe
        val index = itemsVenta.indexOfFirst { it.productoId == producto.id }
        if (index >= 0) {
            val existente = itemsVenta[index]
            // Validar que no exceda el stock
            if (existente.cantidad >= existente.stockDisponible) {
                Toast.makeText(this, "⚠️ Stock máximo alcanzado (${existente.stockDisponible})", Toast.LENGTH_SHORT).show()
                return
            }
            // Incrementar cantidad del existente
            itemsVenta[index] = existente.incrementar()
            adapter.notifyItemChanged(index)
            Toast.makeText(this, "Cantidad: ${itemsVenta[index].cantidad}/${existente.stockDisponible}", Toast.LENGTH_SHORT).show()
        } else {
            // Agregar nuevo producto
            itemsVenta.add(
                ItemVenta(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    precioUnitario = producto.precio,
                    cantidad = 1,
                    stockDisponible = producto.stock,
                    imagenUrl = producto.imagenUrl
                )
            )
            adapter.notifyItemInserted(itemsVenta.size - 1)
            Toast.makeText(this, "✅ ${producto.nombre} (Stock: ${producto.stock})", Toast.LENGTH_SHORT).show()
        }
        updateUI()
    }

    private fun updateUI() {
        tvEmpty.visibility = if (itemsVenta.isEmpty()) View.VISIBLE else View.GONE
        rvProductos.visibility = if (itemsVenta.isEmpty()) View.GONE else View.VISIBLE
        btnConfirmarVenta.isEnabled = itemsVenta.isNotEmpty()
        updateTotals()
    }

    private fun updateTotals() {
        val total = itemsVenta.sumOf { it.subtotal }
        val cantidadItems = itemsVenta.sumOf { it.cantidad }

        tvCantidadItems.text = "$cantidadItems items"
        tvSubtotal.text = "S/ ${String.format("%.2f", total)}"
        tvTotal.text = "S/ ${String.format("%.2f", total)}"
    }

    private fun confirmarVenta() {
        if (itemsVenta.isEmpty()) return

        AlertDialog.Builder(this)
            .setTitle("Confirmar Venta")
            .setMessage("Total: S/ ${String.format("%.2f", itemsVenta.sumOf { it.subtotal })}\n\n¿Confirmar venta presencial?")
            .setPositiveButton("Confirmar") { _, _ -> procesarVenta() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun procesarVenta() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val detalles = itemsVenta.map {
                    DetallePedidoRequest(
                        productoId = it.productoId,
                        cantidad = it.cantidad,
                        precioUnitario = it.precioUnitario
                    )
                }

                val request = CrearPedidoPresencialRequest(
                    detalles = detalles,
                    observaciones = "Venta presencial en farmacia"
                )

                val response = pedidoApiService.crearPedidoPresencial(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@VentaPresencialActivity,
                        "✅ Venta registrada exitosamente", Toast.LENGTH_LONG).show()
                    itemsVenta.clear()
                    adapter.notifyDataSetChanged()
                    updateUI()
                } else {
                    Toast.makeText(this@VentaPresencialActivity,
                        "Error al registrar venta", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VentaPresencialActivity,
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}