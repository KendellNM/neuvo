package com.example.doloresapp.presentation.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.local.entity.NotificacionEntity
import java.text.SimpleDateFormat
import java.util.*

class NotificacionesAdapter(
    private val notificaciones: List<NotificacionEntity>,
    private val onNotificationClick: (NotificacionEntity) -> Unit
) : RecyclerView.Adapter<NotificacionesAdapter.ViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tv_titulo)
        val tvMensaje: TextView = view.findViewById(R.id.tv_mensaje)
        val tvFecha: TextView = view.findViewById(R.id.tv_fecha)
        val tvTipo: TextView = view.findViewById(R.id.tv_tipo)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificacion = notificaciones[position]
        
        holder.tvTitulo.text = notificacion.titulo
        holder.tvMensaje.text = notificacion.mensaje
        holder.tvFecha.text = dateFormat.format(notificacion.fecha)
        holder.tvTipo.text = getTipoEmoji(notificacion.tipo)
        
        // Negrita si no está leída
        if (!notificacion.leida) {
            holder.tvTitulo.setTypeface(null, Typeface.BOLD)
            holder.tvMensaje.setTypeface(null, Typeface.BOLD)
        } else {
            holder.tvTitulo.setTypeface(null, Typeface.NORMAL)
            holder.tvMensaje.setTypeface(null, Typeface.NORMAL)
        }
        
        holder.itemView.setOnClickListener {
            onNotificationClick(notificacion)
        }
    }
    
    override fun getItemCount() = notificaciones.size
    
    private fun getTipoEmoji(tipo: String): String {
        return when (tipo) {
            "PEDIDO" -> "📦"
            "PROMOCION" -> "🎁"
            "RECORDATORIO" -> "⏰"
            "SISTEMA" -> "ℹ️"
            else -> "🔔"
        }
    }
}
