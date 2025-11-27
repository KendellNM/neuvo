package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.RecetaDigitalResponse
import com.example.doloresapp.data.remote.RecetasDigitalesApi
import com.example.doloresapp.presentation.adapter.RecetasPendientesAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecetasPendientesActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvRecetas: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: View
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var tvContador: TextView
    private lateinit var adapter: RecetasPendientesAdapter

    private val recetasApi by lazy {
        NetworkClient.createService(RecetasDigitalesApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas_pendientes)

        NetworkClient.init(this)
        initViews()
        setupRecyclerView()
        cargarRecetas()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        rvRecetas = findViewById(R.id.rvRecetas)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        tvContador = findViewById(R.id.tvContador)

        toolbar.setNavigationOnClickListener { finish() }
        swipeRefresh.setOnRefreshListener { cargarRecetas() }
        swipeRefresh.setColorSchemeResources(R.color.purple_500)
    }

    private fun setupRecyclerView() {
        adapter = RecetasPendientesAdapter(
            onProcesarClick = { receta -> abrirProcesarReceta(receta) },
            onRechazarClick = { receta -> mostrarDialogoRechazar(receta) }
        )
        rvRecetas.layoutManager = LinearLayoutManager(this)
        rvRecetas.adapter = adapter
    }

    private fun cargarRecetas() {
        progressBar.visibility = View.VISIBLE
        emptyState.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = recetasApi.getRecetasPendientes()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false

                    if (response.isSuccessful && response.body()?.success == true) {
                        val recetas = response.body()?.data ?: emptyList()
                        
                        // Actualizar contador
                        tvContador.text = "${recetas.size} pendiente${if (recetas.size != 1) "s" else ""}"
                        
                        if (recetas.isEmpty()) {
                            emptyState.visibility = View.VISIBLE
                            swipeRefresh.visibility = View.GONE
                        } else {
                            emptyState.visibility = View.GONE
                            swipeRefresh.visibility = View.VISIBLE
                            adapter.submitList(recetas)
                        }
                    } else {
                        Toast.makeText(
                            this@RecetasPendientesActivity,
                            "Error al cargar recetas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(
                        this@RecetasPendientesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun abrirProcesarReceta(receta: RecetaDigitalResponse) {
        val intent = Intent(this, ProcesarRecetaActivity::class.java).apply {
            putExtra("RECETA_ID", receta.id)
            putExtra("DIRECCION", receta.direccionEntrega)
            putExtra("TELEFONO", receta.telefonoContacto)
            putExtra("LATITUD", receta.latitud)
            putExtra("LONGITUD", receta.longitud)
            putExtra("TEXTO_EXTRAIDO", receta.textoExtraido)
            putExtra("IMAGEN_URL", receta.imagenUrl)
        }
        startActivity(intent)
    }

    private fun mostrarDialogoRechazar(receta: RecetaDigitalResponse) {
        val input = TextInputEditText(this).apply {
            hint = "Motivo del rechazo"
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("Rechazar Receta #${receta.id}")
            .setMessage("Indica el motivo del rechazo:")
            .setView(input)
            .setPositiveButton("Rechazar") { _, _ ->
                val motivo = input.text.toString().trim()
                if (motivo.isNotEmpty()) {
                    rechazarReceta(receta.id, motivo)
                } else {
                    Toast.makeText(this, "Ingresa un motivo", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun rechazarReceta(recetaId: Long, motivo: String) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = recetasApi.rechazarReceta(recetaId, motivo)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@RecetasPendientesActivity,
                            "Receta rechazada",
                            Toast.LENGTH_SHORT
                        ).show()
                        cargarRecetas() // Recargar lista
                    } else {
                        Toast.makeText(
                            this@RecetasPendientesActivity,
                            "Error al rechazar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@RecetasPendientesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarRecetas()
    }
}
