package com.example.doloresapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.RecetaDigitalResponse
import com.example.doloresapp.utils.Constants
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class RecetasPendientesAdapter(
    private val onProcesarClick: (RecetaDigitalResponse) -> Unit,
    private val onRechazarClick: (RecetaDigitalResponse) -> Unit
) : RecyclerView.Adapter<RecetasPendientesAdapter.ViewHolder>() {

    private val recetas = mutableListOf<RecetaDigitalResponse>()

    fun submitList(list: List<RecetaDigitalResponse>) {
        recetas.clear()
        recetas.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta_pendiente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recetas[position])
    }

    override fun getItemCount() = recetas.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivReceta: ImageView = itemView.findViewById(R.id.ivReceta)
        private val tvRecetaId: TextView = itemView.findViewById(R.id.tvRecetaId)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvTiempo: TextView = itemView.findViewById(R.id.tvTiempo)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        private val tvObservaciones: TextView = itemView.findViewById(R.id.tvObservaciones)
        private val observacionesContainer: View = itemView.findViewById(R.id.observacionesContainer)
        private val btnProcesar: MaterialButton = itemView.findViewById(R.id.btnProcesar)
        private val btnRechazar: MaterialButton = itemView.findViewById(R.id.btnRechazar)

        fun bind(receta: RecetaDigitalResponse) {
            tvRecetaId.text = "Receta #${receta.id}"
            
            // Formatear fecha y calcular tiempo transcurrido
            receta.fechaCreacion?.let {
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(it)
                    tvFecha.text = "📅 ${outputFormat.format(date!!)}"
                    
                    // Calcular tiempo transcurrido
                    val diff = System.currentTimeMillis() - date.time
                    val minutos = diff / (1000 * 60)
                    val horas = minutos / 60
                    tvTiempo.text = when {
                        minutos < 60 -> "Hace $minutos min"
                        horas < 24 -> "Hace $horas h"
                        else -> "Hace ${horas / 24} días"
                    }
                } catch (e: Exception) {
                    tvFecha.text = "📅 $it"
                    tvTiempo.text = ""
                }
            }

            tvDireccion.text = receta.direccionEntrega ?: "Sin dirección"
            tvTelefono.text = receta.telefonoContacto ?: "Sin teléfono"

            if (!receta.observacionesCliente.isNullOrEmpty()) {
                observacionesContainer.visibility = View.VISIBLE
                tvObservaciones.text = receta.observacionesCliente
            } else {
                observacionesContainer.visibility = View.GONE
            }

            // Cargar imagen
            receta.imagenUrl?.let { url ->
                val fullUrl = if (url.startsWith("http")) url else "${Constants.BASE_URL}$url"
                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .centerCrop()
                    .into(ivReceta)
            }

            btnProcesar.setOnClickListener { onProcesarClick(receta) }
            btnRechazar.setOnClickListener { onRechazarClick(receta) }
        }
    }
}
