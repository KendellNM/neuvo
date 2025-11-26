package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.api.FidelizacionApiService
import com.example.doloresapp.di.ServiceLocator
import com.example.doloresapp.domain.model.MovimientoPuntos
import com.example.doloresapp.domain.model.ProgramaFidelizacion
import com.example.doloresapp.presentation.adapters.MovimientosAdapter
import com.example.doloresapp.utils.ApiConstants
import kotlinx.coroutines.launch

class FidelizacionActivity : AppCompatActivity() {
    
    private lateinit var apiService: FidelizacionApiService
    private lateinit var tvPuntos: TextView
    private lateinit var tvNivel: TextView
    private lateinit var tvSiguienteNivel: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressNivel: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCanjear: Button
    
    private var clienteId: Long = 0
    private var puntosActuales: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fidelizacion)
        
        initViews()
        apiService = ServiceLocator.provideFidelizacionApiService()
        
        // Obtener clienteId del usuario actual
        loadClienteIdAndData()
    }
    
    private fun loadClienteIdAndData() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val userApi = com.example.doloresapp.data.remote.NetworkClient.createService(
                    com.example.doloresapp.data.remote.UserApi::class.java
                )
                val currentUser = userApi.getCurrentUser()
                clienteId = currentUser.clienteId ?: 0L
                
                if (clienteId == 0L) {
                    Toast.makeText(this@FidelizacionActivity, "Error: Usuario no identificado como cliente", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }
                
                loadPuntos()
                loadHistorial()
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@FidelizacionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun initViews() {
        tvPuntos = findViewById(R.id.tv_puntos)
        tvNivel = findViewById(R.id.tv_nivel)
        tvSiguienteNivel = findViewById(R.id.tv_siguiente_nivel)
        progressBar = findViewById(R.id.progress_bar)
        progressNivel = findViewById(R.id.progress_nivel)
        recyclerView = findViewById(R.id.recycler_movimientos)
        btnCanjear = findViewById(R.id.btn_canjear)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        btnCanjear.setOnClickListener {
            mostrarDialogoCanjear()
        }
    }
    
    private fun loadPuntos() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val response = apiService.getPuntos(clienteId)
                
                if (response.isSuccessful && response.body() != null) {
                    val programa = response.body()!!
                    mostrarPuntos(programa)
                } else {
                    // Intentar crear programa si no existe
                    crearPrograma()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FidelizacionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun crearPrograma() {
        lifecycleScope.launch {
            try {
                val response = apiService.crearPrograma(clienteId)
                if (response.isSuccessful && response.body() != null) {
                    mostrarPuntos(response.body()!!)
                }
            } catch (e: Exception) {
                Toast.makeText(this@FidelizacionActivity, "Error creando programa: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun mostrarPuntos(programa: ProgramaFidelizacion) {
        puntosActuales = programa.puntosActuales
        tvPuntos.text = "${programa.puntosActuales} puntos"
        tvNivel.text = getNivelEmoji(programa.nivelMembresia) + " " + programa.nivelMembresia
        
        if (programa.siguienteNivel != null) {
            tvSiguienteNivel.text = "Faltan ${programa.puntosParaSiguienteNivel} puntos para ${programa.siguienteNivel}"
            
            // Calcular progreso
            val puntosParaNivel = getPuntosParaNivel(programa.nivelMembresia)
            val puntosParaSiguiente = getPuntosParaNivel(programa.siguienteNivel!!)
            val progreso = ((programa.puntosActuales - puntosParaNivel).toFloat() / 
                           (puntosParaSiguiente - puntosParaNivel) * 100).toInt()
            progressNivel.progress = progreso
        } else {
            tvSiguienteNivel.text = "¡Nivel máximo alcanzado! 🎉"
            progressNivel.progress = 100
        }
    }
    
    private fun loadHistorial() {
        lifecycleScope.launch {
            try {
                val response = apiService.getHistorial(clienteId)
                if (response.isSuccessful && response.body() != null) {
                    val movimientos = response.body()!!
                    recyclerView.adapter = MovimientosAdapter(movimientos)
                }
            } catch (e: Exception) {
                Toast.makeText(this@FidelizacionActivity, "Error cargando historial: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun mostrarDialogoCanjear() {
        val dialog = android.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_canjear_puntos, null)
        
        val etPuntos = view.findViewById<EditText>(R.id.et_puntos)
        val tvDisponibles = view.findViewById<TextView>(R.id.tv_disponibles)
        
        tvDisponibles.text = "Puntos disponibles: $puntosActuales"
        
        dialog.setView(view)
        dialog.setTitle("Canjear Puntos")
        dialog.setPositiveButton("Canjear") { _, _ ->
            val puntos = etPuntos.text.toString().toIntOrNull()
            if (puntos != null && puntos > 0 && puntos <= puntosActuales) {
                canjearPuntos(puntos)
            } else {
                Toast.makeText(this, "Puntos inválidos", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setNegativeButton("Cancelar", null)
        dialog.show()
    }
    
    private fun canjearPuntos(puntos: Int) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                
                val response = apiService.canjearPuntos(
                    com.example.doloresapp.data.remote.dto.CanjearPuntosRequest(
                        clienteId = clienteId, 
                        puntos = puntos, 
                        codigoCupon = "DESCUENTO_${puntos / 10}"
                    )
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@FidelizacionActivity, "¡Puntos canjeados exitosamente!", Toast.LENGTH_SHORT).show()
                    loadPuntos()
                    loadHistorial()
                } else {
                    Toast.makeText(this@FidelizacionActivity, response.body()?.error ?: "Error", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FidelizacionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun getNivelEmoji(nivel: String): String {
        return when (nivel) {
            ApiConstants.NivelMembresia.BRONCE -> "🥉"
            ApiConstants.NivelMembresia.PLATA -> "🥈"
            ApiConstants.NivelMembresia.ORO -> "🥇"
            ApiConstants.NivelMembresia.PLATINO -> "💎"
            else -> "⭐"
        }
    }
    
    private fun getPuntosParaNivel(nivel: String): Int {
        return when (nivel) {
            ApiConstants.NivelMembresia.BRONCE -> 0
            ApiConstants.NivelMembresia.PLATA -> 2000
            ApiConstants.NivelMembresia.ORO -> 5000
            ApiConstants.NivelMembresia.PLATINO -> 10000
            else -> 0
        }
    }
}
