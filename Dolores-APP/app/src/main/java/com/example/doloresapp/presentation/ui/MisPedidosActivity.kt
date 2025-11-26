package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
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
import com.example.doloresapp.presentation.adapter.PedidosAdapter
import com.example.doloresapp.utils.ApiConstants
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class MisPedidosActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: PedidosAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos)
        
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
                val intent = android.content.Intent(this, DeliveryTrackingActivity::class.java)
                intent.putExtra("pedido_id", pedido.id)
                // Pasar coordenadas de destino si están disponibles
                pedido.direccionInfo?.let { dir ->
                    dir.getLatitudDouble()?.let { intent.putExtra("destino_lat", it) }
                    dir.getLongitudDouble()?.let { intent.putExtra("destino_lng", it) }
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Pedido #${pedido.id} - ${pedido.estado}", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        cargarPedidos()
    }
    
    private fun cargarPedidos() {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // Obtener el clienteId del usuario actual
                val userApi = NetworkClient.createService(com.example.doloresapp.data.remote.UserApi::class.java)
                val currentUser = userApi.getCurrentUser()
                val clienteId = currentUser.clienteId
                
                if (clienteId == null) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MisPedidosActivity, "No se encontró información del cliente", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val api = NetworkClient.createService(PedidoApiService::class.java)
                val pedidos = api.getPedidosByCliente(clienteId)
                
                progressBar.visibility = View.GONE
                
                if (pedidos.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(pedidos)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MisPedidosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
