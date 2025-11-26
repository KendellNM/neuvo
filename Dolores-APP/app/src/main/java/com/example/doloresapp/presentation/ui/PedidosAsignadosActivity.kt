package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.net.Uri
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
import com.example.doloresapp.presentation.adapters.PedidosRepartidorAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class PedidosAsignadosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyState: View
    private lateinit var progressBar: View
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: PedidosRepartidorAdapter
    private lateinit var api: PedidosApi
    
    private var allPedidos: List<PedidoResponse> = emptyList()
    private var currentFilter = "ASIGNADO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos_asignados)

        // Asegurar que NetworkClient esté inicializado
        com.example.doloresapp.data.local.TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)

        setupToolbar()
        setupViews()
        setupRecyclerView()
        setupTabs()
        
        api = NetworkClient.createService(PedidosApi::class.java)
        
        // Seleccionar tab inicial si se especifica
        val tabInicial = intent.getIntExtra("tab_inicial", 0)
        if (tabInicial in 0..2) {
            tabLayout.getTabAt(tabInicial)?.select()
            currentFilter = when (tabInicial) {
                0 -> "ASIGNADO"
                1 -> "EN_CAMINO"
                2 -> "ENTREGADO"
                else -> "ASIGNADO"
            }
        }
        
        loadPedidos()
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
        tabLayout = findViewById(R.id.tab_layout)

        swipeRefresh.setOnRefreshListener { loadPedidos() }
        swipeRefresh.setColorSchemeResources(R.color.dolores)
    }

    private fun setupRecyclerView() {
        adapter = PedidosRepartidorAdapter(
            onVerMapa = { pedido -> abrirMapa(pedido) },
            onIniciarEntrega = { pedido -> iniciarEntrega(pedido) },
            onMarcarEntregado = { pedido -> marcarEntregado(pedido) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentFilter = when (tab?.position) {
                    0 -> "ASIGNADO"
                    1 -> "EN_CAMINO"
                    2 -> "ENTREGADO"
                    else -> "ASIGNADO"
                }
                filterPedidos()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadPedidos() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                
                // Obtener el ID del repartidor actual
                val userApi = NetworkClient.createService(com.example.doloresapp.data.remote.UserApi::class.java)
                val currentUser = try {
                    userApi.getCurrentUser()
                } catch (e: Exception) {
                    android.util.Log.e("PedidosAsignados", "Error obteniendo usuario: ${e.message}")
                    null
                }
                
                val repartidorId = currentUser?.repartidorId
                android.util.Log.d("PedidosAsignados", "Usuario: ${currentUser?.usuario}, RepartidorId: $repartidorId")
                
                allPedidos = if (repartidorId != null) {
                    // Cargar solo los pedidos asignados a este repartidor
                    try {
                        api.getPedidosByRepartidor(repartidorId)
                    } catch (e: Exception) {
                        android.util.Log.e("PedidosAsignados", "Error cargando pedidos del repartidor: ${e.message}")
                        emptyList()
                    }
                } else {
                    // Fallback: cargar todos los pedidos (para pruebas)
                    android.util.Log.w("PedidosAsignados", "No se encontró repartidorId, cargando todos los pedidos")
                    try {
                        api.getAllPedidos()
                    } catch (e: Exception) {
                        android.util.Log.e("PedidosAsignados", "Error cargando todos los pedidos: ${e.message}")
                        emptyList()
                    }
                }
                
                filterPedidos()
            } catch (e: Exception) {
                android.util.Log.e("PedidosAsignados", "Error general: ${e.message}", e)
                Toast.makeText(this@PedidosAsignadosActivity, 
                    "Error al cargar pedidos: ${e.message}", Toast.LENGTH_SHORT).show()
                allPedidos = emptyList()
                filterPedidos()
            } finally {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun filterPedidos() {
        val filtered = allPedidos.filter { 
            it.estado?.uppercase() == currentFilter 
        }
        adapter.submitList(filtered)
        emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun abrirMapa(pedido: PedidoResponse) {
        val lat = pedido.direcciones?.getLatitudDouble() ?: -12.0464
        val lng = pedido.direcciones?.getLongitudDouble() ?: -77.0428
        val direccion = pedido.direcciones?.direccion ?: "Destino"
        
        // Abrir Google Maps con navegación
        val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Si no tiene Google Maps, abrir en navegador
            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    private fun iniciarEntrega(pedido: PedidoResponse) {
        val direccionTexto = pedido.direcciones?.direccion ?: "Sin dirección"
        val clienteNombre = pedido.clientes?.let { "${it.nombres ?: ""} ${it.apellidos ?: ""}".trim() } ?: "Cliente"
        
        AlertDialog.Builder(this)
            .setTitle("Iniciar Entrega")
            .setMessage("¿Deseas iniciar la entrega del pedido #${pedido.numeroPedido ?: pedido.idPedidos}?\n\n📍 $direccionTexto\n👤 $clienteNombre")
            .setPositiveButton("Sí, iniciar") { _, _ ->
                cambiarEstadoPedido(pedido, "EN_CAMINO")
                
                // Abrir el tracking GPS con coordenadas
                val intent = Intent(this, RepartidorActivity::class.java)
                intent.putExtra("pedido_id", pedido.idPedidos)
                intent.putExtra("destino_lat", pedido.direcciones?.getLatitudDouble() ?: -12.0464)
                intent.putExtra("destino_lng", pedido.direcciones?.getLongitudDouble() ?: -77.0428)
                intent.putExtra("cliente_nombre", clienteNombre)
                intent.putExtra("direccion_texto", direccionTexto)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun marcarEntregado(pedido: PedidoResponse) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Entrega")
            .setMessage("¿Confirmas que el pedido #${pedido.numeroPedido ?: pedido.idPedidos} fue entregado?")
            .setPositiveButton("Sí, entregado") { _, _ ->
                cambiarEstadoPedido(pedido, "ENTREGADO")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEstadoPedido(pedido: PedidoResponse, nuevoEstado: String) {
        lifecycleScope.launch {
            try {
                api.cambiarEstado(pedido.idPedidos, nuevoEstado)
                Toast.makeText(this@PedidosAsignadosActivity, 
                    "Estado actualizado a $nuevoEstado", Toast.LENGTH_SHORT).show()
                loadPedidos()
            } catch (e: Exception) {
                Toast.makeText(this@PedidosAsignadosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
