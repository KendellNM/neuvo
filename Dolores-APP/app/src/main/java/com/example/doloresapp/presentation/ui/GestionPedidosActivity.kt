package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.PedidoResponse
import com.example.doloresapp.data.remote.PedidosApi
import com.example.doloresapp.data.remote.RepartidoresApi
import com.example.doloresapp.data.remote.RepartidorResponse
import com.example.doloresapp.presentation.adapters.PedidosAdminAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class GestionPedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyState: View
    private lateinit var progressBar: View
    private lateinit var chipGroup: ChipGroup
    private lateinit var adapter: PedidosAdminAdapter
    private lateinit var api: PedidosApi
    
    private var allPedidos: List<PedidoResponse> = emptyList()
    private var repartidores: List<RepartidorResponse> = emptyList()
    private var currentFilter: String? = null
    private lateinit var repartidoresApi: RepartidoresApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_pedidos)

        // Asegurar que NetworkClient esté inicializado
        com.example.doloresapp.data.local.TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)

        setupToolbar()
        setupViews()
        setupRecyclerView()
        setupChips()
        
        api = NetworkClient.createService(PedidosApi::class.java)
        repartidoresApi = NetworkClient.createService(RepartidoresApi::class.java)
        loadPedidos()
        loadRepartidores()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recycler_pedidos)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        emptyState = findViewById(R.id.empty_state)
        progressBar = findViewById(R.id.progress_bar)
        chipGroup = findViewById(R.id.chip_group_estados)

        swipeRefresh.setOnRefreshListener { loadPedidos() }
        swipeRefresh.setColorSchemeResources(R.color.dolores)
    }

    private fun setupRecyclerView() {
        adapter = PedidosAdminAdapter(
            onCambiarEstado = { pedido -> mostrarDialogoCambiarEstado(pedido) },
            onAsignar = { pedido -> mostrarDialogoAsignar(pedido) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupChips() {
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.chip_todos) -> null
                checkedIds.contains(R.id.chip_pendiente) -> "PENDIENTE"
                checkedIds.contains(R.id.chip_preparando) -> "PREPARANDO"
                checkedIds.contains(R.id.chip_listo) -> "LISTO"
                checkedIds.contains(R.id.chip_en_camino) -> "EN_CAMINO"
                checkedIds.contains(R.id.chip_entregado) -> "ENTREGADO"
                else -> null
            }
            filterPedidos()
        }
    }

    private fun loadPedidos() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                allPedidos = api.getAllPedidos()
                filterPedidos()
            } catch (e: Exception) {
                Toast.makeText(this@GestionPedidosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun filterPedidos() {
        val filtered = if (currentFilter == null) {
            allPedidos
        } else {
            allPedidos.filter { it.estado?.uppercase() == currentFilter }
        }
        adapter.submitList(filtered)
        emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun mostrarDialogoCambiarEstado(pedido: PedidoResponse) {
        val estados = arrayOf("PENDIENTE", "CONFIRMADO", "PREPARANDO", "LISTO", "ASIGNADO", "EN_CAMINO", "ENTREGADO", "CANCELADO")
        
        AlertDialog.Builder(this)
            .setTitle("Cambiar Estado")
            .setItems(estados) { _, which ->
                cambiarEstado(pedido, estados[which])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun loadRepartidores() {
        lifecycleScope.launch {
            try {
                repartidores = repartidoresApi.getAllRepartidores()
            } catch (e: Exception) {
                // Silenciar error, se mostrará al intentar asignar
            }
        }
    }

    private fun mostrarDialogoAsignar(pedido: PedidoResponse) {
        if (repartidores.isEmpty()) {
            Toast.makeText(this, "No hay repartidores disponibles", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Crear lista de nombres de repartidores
        val nombres = repartidores.map { 
            "${it.nombres ?: ""} ${it.apellidos ?: ""}".trim().ifEmpty { "Repartidor ${it.idRepartidores}" }
        }.toTypedArray()
        
        AlertDialog.Builder(this)
            .setTitle("Seleccionar Repartidor")
            .setItems(nombres) { _, which ->
                val repartidorSeleccionado = repartidores[which]
                asignarPedido(pedido, repartidorSeleccionado.idRepartidores)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEstado(pedido: PedidoResponse, nuevoEstado: String) {
        lifecycleScope.launch {
            try {
                api.cambiarEstado(pedido.idPedidos, nuevoEstado)
                Toast.makeText(this@GestionPedidosActivity, 
                    "Estado cambiado a $nuevoEstado", Toast.LENGTH_SHORT).show()
                loadPedidos()
            } catch (e: Exception) {
                Toast.makeText(this@GestionPedidosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun asignarPedido(pedido: PedidoResponse, repartidorId: Long) {
        lifecycleScope.launch {
            try {
                api.asignarPedido(pedido.idPedidos, repartidorId)
                Toast.makeText(this@GestionPedidosActivity, 
                    "Pedido asignado correctamente", Toast.LENGTH_SHORT).show()
                loadPedidos()
            } catch (e: Exception) {
                Toast.makeText(this@GestionPedidosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
