package com.example.doloresapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.domain.model.MovimientoPuntos

class MovimientosAdapter(
    private val movimientos: List<MovimientoPuntos>
) : RecyclerView.Adapter<MovimientosAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTipo: TextView = view.findViewById(R.id.tv_tipo)
        val tvPuntos: TextView = view.findViewById(R.id.tv_puntos)
        val tvDescripcion: TextView = view.findViewById(R.id.tv_descripcion)
        val tvFecha: TextView = view.findViewById(R.id.tv_fecha)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento_puntos, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movimiento = movimientos[position]
        
        holder.tvTipo.text = if (movimiento.tipo == "ACUMULACION") "➕" else "➖"
        holder.tvPuntos.text = "${if (movimiento.puntos > 0) "+" else ""}${movimiento.puntos} pts"
        holder.tvDescripcion.text = movimiento.descripcion
        holder.tvFecha.text = movimiento.fecha.substring(0, 10) // Solo fecha
        
        // Color según tipo
        val color = if (movimiento.tipo == "ACUMULACION") {
            android.graphics.Color.parseColor("#4CAF50")
        } else {
            android.graphics.Color.parseColor("#F44336")
        }
        holder.tvPuntos.setTextColor(color)
    }
    
    override fun getItemCount() = movimientos.size
}
