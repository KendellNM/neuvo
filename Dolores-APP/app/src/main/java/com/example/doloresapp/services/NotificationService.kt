package com.example.doloresapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.doloresapp.MainActivity
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.NotificacionEntity
import com.example.doloresapp.data.remote.api.NotificacionApiService
import com.example.doloresapp.di.ServiceLocator
import com.example.doloresapp.utils.ApiConstants
import kotlinx.coroutines.*
import java.util.Date

/**
 * Servicio en background para obtener notificaciones del backend
 * sin necesidad de Firebase
 */
class NotificationService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var apiService: NotificacionApiService
    private lateinit var database: AppDatabase
    private var clienteId: Long = 0
    
    companion object {
        private const val CHANNEL_ID = "farmacia_dolores_channel"
        private const val NOTIFICATION_ID = 1001
        private const val POLL_INTERVAL = 60_000L // 1 minuto
        
        fun start(context: Context) {
            val intent = Intent(context, NotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, NotificationService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        
        apiService = ServiceLocator.provideNotificacionApiService()
        database = AppDatabase.getDatabase(this)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, Context.MODE_PRIVATE)
        clienteId = prefs.getLong(ApiConstants.Prefs.USER_ID, 0)
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createForegroundNotification())
        
        // Iniciar polling
        startPolling()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones Farmacia",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de pedidos y promociones"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Farmacia Dolores")
            .setContentText("Escuchando notificaciones...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun startPolling() {
        serviceScope.launch {
            while (isActive) {
                try {
                    checkForNewNotifications()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(POLL_INTERVAL)
            }
        }
    }
    
    private suspend fun checkForNewNotifications() {
        if (clienteId == 0L) return
        
        try {
            val response = apiService.getNotificaciones(clienteId)
            if (response.isSuccessful && response.body() != null) {
                val notificaciones = response.body()!!
                
                // Obtener última notificación guardada
                val lastNotificationId = getLastNotificationId()
                
                // Filtrar solo nuevas notificaciones
                val nuevas = notificaciones.filter { it.id > lastNotificationId && !it.leida }
                
                // Mostrar notificaciones
                nuevas.forEach { notificacion ->
                    showNotification(notificacion.id, notificacion.titulo, notificacion.mensaje)
                    saveNotificationToDatabase(notificacion)
                }
                
                // Actualizar última ID
                if (nuevas.isNotEmpty()) {
                    saveLastNotificationId(nuevas.maxOf { it.id })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showNotification(id: Long, title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id.toInt(), notification)
    }
    
    private suspend fun saveNotificationToDatabase(notificacion: com.example.doloresapp.domain.model.Notificacion) {
        try {
            val entity = NotificacionEntity(
                id = notificacion.id,
                titulo = notificacion.titulo,
                mensaje = notificacion.mensaje,
                tipo = notificacion.tipo,
                leida = notificacion.leida,
                fecha = Date(),
                datos = notificacion.datos?.toString()
            )
            database.notificacionDao().insertNotificacion(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getLastNotificationId(): Long {
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, Context.MODE_PRIVATE)
        return prefs.getLong("last_notification_id", 0)
    }
    
    private fun saveLastNotificationId(id: Long) {
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong("last_notification_id", id).apply()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
