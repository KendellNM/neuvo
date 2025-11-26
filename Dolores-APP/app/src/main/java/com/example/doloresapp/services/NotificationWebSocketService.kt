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
import com.example.doloresapp.utils.ApiConstants
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.Date

/**
 * Servicio que usa WebSocket para recibir notificaciones en tiempo real
 * Alternativa más eficiente que polling
 */
class NotificationWebSocketService : Service() {
    
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private val gson = Gson()
    private lateinit var database: AppDatabase
    private var clienteId: Long = 0
    
    companion object {
        private const val CHANNEL_ID = "farmacia_dolores_channel"
        private const val NOTIFICATION_ID = 1002
        
        fun start(context: Context) {
            val intent = Intent(context, NotificationWebSocketService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, NotificationWebSocketService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        
        database = AppDatabase.getDatabase(this)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, Context.MODE_PRIVATE)
        clienteId = prefs.getLong(ApiConstants.Prefs.USER_ID, 0)
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createForegroundNotification())
        
        connectWebSocket()
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
            .setContentText("Conectado a notificaciones en tiempo real")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun connectWebSocket() {
        if (clienteId == 0L) return
        
        // Conectar a WebSocket de notificaciones
        // Nota: El backend debe tener un endpoint /ws-notifications
        val url = "${ApiConstants.BASE_URL}ws-notifications"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        
        val lifecycleDisposable = stompClient?.lifecycle()?.subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    subscribeToNotifications()
                }
                LifecycleEvent.Type.ERROR -> {
                    // Intentar reconectar después de 5 segundos
                    android.os.Handler(mainLooper).postDelayed({
                        connectWebSocket()
                    }, 5000)
                }
                else -> {}
            }
        }
        
        lifecycleDisposable?.let { compositeDisposable.add(it) }
        stompClient?.connect()
    }
    
    private fun subscribeToNotifications() {
        // Suscribirse a notificaciones del cliente
        stompClient?.topic("/topic/notifications/$clienteId")?.subscribe { message ->
            try {
                val notification = gson.fromJson(message.payload, NotificationMessage::class.java)
                showNotification(notification)
                saveToDatabase(notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }?.let { compositeDisposable.add(it) }
    }
    
    private fun showNotification(notification: NotificationMessage) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_id", notification.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            notification.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notification.titulo)
            .setContentText(notification.mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification.id.toInt(), notif)
    }
    
    private fun saveToDatabase(notification: NotificationMessage) {
        Thread {
            try {
                val entity = NotificacionEntity(
                    id = notification.id,
                    titulo = notification.titulo,
                    mensaje = notification.mensaje,
                    tipo = notification.tipo,
                    leida = false,
                    fecha = Date(),
                    datos = notification.datos
                )
                
                // Usar coroutine para llamar función suspend
                CoroutineScope(Dispatchers.IO).launch {
                    database.notificacionDao().insertNotificacion(entity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        stompClient?.disconnect()
    }
    
    data class NotificationMessage(
        val id: Long,
        val titulo: String,
        val mensaje: String,
        val tipo: String,
        val datos: String?
    )
}
