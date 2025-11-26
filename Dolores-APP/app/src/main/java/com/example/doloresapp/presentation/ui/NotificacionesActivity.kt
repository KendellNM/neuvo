package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.presentation.adapters.NotificacionesAdapter
import com.example.doloresapp.services.NotificationService
import com.example.doloresapp.services.NotificationWebSocketService
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificacionesActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnIniciarServicio: MaterialButton
    private lateinit var btnDetenerServicio: MaterialButton
    private lateinit var database: AppDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)
        
        initViews()
        database = AppDatabase.getDatabase(this)
        
        loadNotificaciones()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_notificaciones)
        progressBar = findViewById(R.id.progress_bar)
        btnIniciarServicio = findViewById(R.id.btn_iniciar_servicio)
        btnDetenerServicio = findViewById(R.id.btn_detener_servicio)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        btnIniciarServicio.setOnClickListener {
            // Opción 1: Usar Polling (más simple)
            NotificationService.start(this)
            
            // Opción 2: Usar WebSocket (más eficiente, requiere backend configurado)
            // NotificationWebSocketService.start(this)
            
            it.isEnabled = false
            btnDetenerServicio.isEnabled = true
        }
        
        btnDetenerServicio.setOnClickListener {
            NotificationService.stop(this)
            // NotificationWebSocketService.stop(this)
            
            it.isEnabled = false
            btnIniciarServicio.isEnabled = true
        }
    }
    
    private fun loadNotificaciones() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            
            database.notificacionDao().getAllNotificaciones().collect { notificaciones ->
                recyclerView.adapter = NotificacionesAdapter(notificaciones) { notificacion ->
                    // Marcar como leída
                    lifecycleScope.launch {
                        database.notificacionDao().marcarComoLeida(notificacion.id)
                    }
                }
                progressBar.visibility = View.GONE
            }
        }
    }
}
