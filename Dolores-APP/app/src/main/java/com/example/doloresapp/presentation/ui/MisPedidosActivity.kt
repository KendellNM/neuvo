package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.PedidoApiService
import com.example.doloresapp.data.sync.SyncManager
import com.example.doloresapp.presentation.adapter.PedidosAdapter
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.NetworkUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MisPedidosActivity : AppCompatActivity() {
    
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var tvEmpty: TextView? = null
    private var adapter: PedidosAdapter? = null
    
    // Sección de pedidos pendientes offline
    private var layoutPendientes: LinearLayout? = null
    private var tvPendientesCount: TextView? = null
    private var btnSincronizar: MaterialButton? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos)
        
        try {
            // Toolbar
            val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Mis Pedidos"
            
            // Views
            recyclerView = findViewById(R.id.recyclerPedidos)
            progressBar = findViewById(R.id.progressBar)
            tvEmpty = findViewById(R.id.tvEmpty)
            
            // Adapter
            adapter = PedidosAdapter { pedido ->
                // Click en pedido - ver detalle o tracking
                val estado = pedido.estado?.uppercase() ?: ""
                if (estado == "EN_CAMINO" || estado == "ASIGNADO") {
                    try {
                        val intent = android.content.Intent(this, DeliveryTrackingActivity::class.java)
                        intent.putExtra("pedido_id", pedido.id)
                        // Pasar coordenadas de destino si están disponibles
                        pedido.direccionInfo?.let { dir ->
                            dir.getLatitudDouble()?.let { intent.putExtra("destino_lat", it) }
                            dir.getLongitudDouble()?.let { intent.putExtra("destino_lng", it) }
                            dir.direccion?.let { intent.putExtra("direccion", it) }
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error al abrir seguimiento", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Pedido #${pedido.id} - ${pedido.estado}", Toast.LENGTH_SHORT).show()
                }
            }
            recyclerView?.layoutManager = LinearLayoutManager(this)
            recyclerView?.adapter = adapter
            
            // Sección de pedidos pendientes
            layoutPendientes = findViewById(R.id.layoutPendientes)
            tvPendientesCount = findViewById(R.id.tvPendientesCount)
            btnSincronizar = findViewById(R.id.btnSincronizar)
            
            setupPendientesSection()
            cargarPedidos()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupPendientesSection() {
        val syncManager = SyncManager.getInstance(this)
        
        // Observar pedidos pendientes
        lifecycleScope.launch {
            syncManager.pendingCount.collectLatest { count ->
                runOnUiThread {
                    if (count > 0) {
                        layoutPendientes?.visibility = View.VISIBLE
                        tvPendientesCount?.text = "$count pedido(s) esperando conexión"
                    } else {
                        layoutPendientes?.visibility = View.GONE
                    }
                }
            }
        }
        
        // Botón sincronizar
        btnSincronizar?.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                lifecycleScope.launch {
                    btnSincronizar?.isEnabled = false
                    btnSincronizar?.text = "Sincronizando..."
                    
                    val result = syncManager.syncPendingOrders()
                    
                    runOnUiThread {
                        btnSincronizar?.isEnabled = true
                        btnSincronizar?.text = "Sincronizar ahora"
                        
                        if (result.successCount > 0) {
                            Toast.makeText(
                                this@MisPedidosActivity,
                                "✅ ${result.successCount} pedido(s) sincronizado(s)",
                                Toast.LENGTH_SHORT
                            ).show()
                            cargarPedidos() // Recargar lista
                        } else if (result.failCount > 0) {
                            Toast.makeText(
                                this@MisPedidosActivity,
                                "⚠️ Error sincronizando pedidos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Sin conexión a internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun cargarPedidos() {
        progressBar?.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // Obtener el clienteId del usuario actual
                val userApi = NetworkClient.createService(com.example.doloresapp.data.remote.UserApi::class.java)
                val currentUser = userApi.getCurrentUser()
                val clienteId = currentUser.clienteId
                
                if (clienteId == null || clienteId == 0L) {
                    runOnUiThread {
                        progressBar?.visibility = View.GONE
                        tvEmpty?.text = "No tienes pedidos aún"
                        tvEmpty?.visibility = View.VISIBLE
                        recyclerView?.visibility = View.GONE
                    }
                    return@launch
                }
                
                val api = NetworkClient.createService(PedidoApiService::class.java)
                val pedidos = api.getPedidosByCliente(clienteId)
                
                runOnUiThread {
                    progressBar?.visibility = View.GONE
                    
                    if (pedidos.isEmpty()) {
                        tvEmpty?.text = "No tienes pedidos aún"
                        tvEmpty?.visibility = View.VISIBLE
                        recyclerView?.visibility = View.GONE
                    } else {
                        tvEmpty?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        adapter?.submitList(pedidos)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    progressBar?.visibility = View.GONE
                    
                    // Verificar si estamos offline
                    if (!NetworkUtils.isNetworkAvailable(this@MisPedidosActivity)) {
                        tvEmpty?.text = "📴 Sin conexión\nTus pedidos se mostrarán cuando tengas internet"
                    } else {
                        tvEmpty?.text = "No hay pedidos disponibles"
                    }
                    tvEmpty?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
