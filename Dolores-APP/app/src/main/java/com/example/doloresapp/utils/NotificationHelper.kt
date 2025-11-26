package com.example.doloresapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.doloresapp.MainActivity
import com.example.doloresapp.R

/**
 * Helper para crear notificaciones locales fácilmente
 */
object NotificationHelper {
    
    private const val CHANNEL_ID = "farmacia_dolores_channel"
    private const val CHANNEL_NAME = "Notificaciones Farmacia"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de pedidos, promociones y recordatorios"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Muestra una notificación simple
     */
    fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String,
        autoCancel: Boolean = true
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(autoCancel)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }
    
    /**
     * Notificación de pedido listo
     */
    fun notifyPedidoListo(context: Context, pedidoId: Long) {
        showNotification(
            context,
            pedidoId.toInt(),
            "¡Pedido Listo! 🎉",
            "Tu pedido #$pedidoId está listo para recoger"
        )
    }
    
    /**
     * Notificación de pedido en camino
     */
    fun notifyPedidoEnCamino(context: Context, pedidoId: Long) {
        showNotification(
            context,
            pedidoId.toInt(),
            "Pedido en Camino 🚚",
            "Tu pedido #$pedidoId está siendo entregado"
        )
    }
    
    /**
     * Notificación de promoción
     */
    fun notifyPromocion(context: Context, titulo: String, mensaje: String) {
        showNotification(
            context,
            System.currentTimeMillis().toInt(),
            "🎁 $titulo",
            mensaje
        )
    }
    
    /**
     * Notificación de puntos acumulados
     */
    fun notifyPuntosAcumulados(context: Context, puntos: Int) {
        showNotification(
            context,
            System.currentTimeMillis().toInt(),
            "¡Puntos Acumulados! ⭐",
            "Has ganado $puntos puntos de fidelización"
        )
    }
    
    /**
     * Notificación de receta procesada
     */
    fun notifyRecetaProcesada(context: Context, recetaId: Long, estado: String) {
        val mensaje = when (estado) {
            "VALIDADA" -> "Tu receta ha sido validada ✅"
            "RECHAZADA" -> "Tu receta necesita revisión ⚠️"
            else -> "Tu receta está siendo procesada"
        }
        
        showNotification(
            context,
            recetaId.toInt(),
            "Receta Digital",
            mensaje
        )
    }
    
    /**
     * Cancela una notificación específica
     */
    fun cancelNotification(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }
    
    /**
     * Cancela todas las notificaciones
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}
