package com.example.doloresapp.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.UsuarioAdmin
import com.google.android.material.button.MaterialButton

data class UsuarioConRol(
    val usuario: UsuarioAdmin,
    val rolNombre: String? = null,
    val usuarioRolId: Long? = null
)

class UsuariosAdminAdapter(
    private val onAsignarRol: (UsuarioConRol) -> Unit,
    private val onEditar: (UsuarioConRol) -> Unit
) : ListAdapter<UsuarioConRol, UsuariosAdminAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsuario: TextView = itemView.findViewById(R.id.tv_usuario)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado)
        private val tvCorreo: TextView = itemView.findViewById(R.id.tv_correo)
        private val tvRol: TextView = itemView.findViewById(R.id.tv_rol)
        private val btnAsignarRol: MaterialButton = itemView.findViewById(R.id.btn_asignar_rol)
        private val btnEditar: MaterialButton = itemView.findViewById(R.id.btn_editar)

        fun bind(item: UsuarioConRol) {
            val usuario = item.usuario
            
            tvUsuario.text = usuario.usuario ?: "Sin nombre"
            tvCorreo.text = "📧 ${usuario.correo ?: "Sin correo"}"
            tvEstado.text = usuario.estado ?: "ACTIVO"
            
            // Color del estado
            val estadoColor = when (usuario.estado?.uppercase()) {
                "ACTIVO" -> Color.parseColor("#4CAF50")
                "INACTIVO" -> Color.parseColor("#9E9E9E")
                else -> Color.parseColor("#FF9800")
            }
            tvEstado.background?.setTint(estadoColor)
            
            // Mostrar rol
            tvRol.text = if (item.rolNombre != null) {
                "🎭 ${item.rolNombre}"
            } else {
                "🎭 Sin rol asignado"
            }
            tvRol.setTextColor(
                if (item.rolNombre != null) Color.parseColor("#4CAF50") 
                else Color.parseColor("#FF9800")
            )
            
            btnAsignarRol.setOnClickListener { onAsignarRol(item) }
            btnEditar.setOnClickListener { onEditar(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UsuarioConRol>() {
        override fun areItemsTheSame(oldItem: UsuarioConRol, newItem: UsuarioConRol) =
            oldItem.usuario.idUsuarios == newItem.usuario.idUsuarios

        override fun areContentsTheSame(oldItem: UsuarioConRol, newItem: UsuarioConRol) =
            oldItem == newItem
    }
}
