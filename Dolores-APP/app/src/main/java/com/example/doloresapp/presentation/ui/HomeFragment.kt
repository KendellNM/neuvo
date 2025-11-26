package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.doloresapp.R
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.UserApi
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.home_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencia al TextView de bienvenida
        val tvWelcome: TextView? = view.findViewById(R.id.tvWelcome)

        // Llamar al endpoint para obtener el usuario actual y actualizar el saludo
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = NetworkClient.createService(UserApi::class.java)
                val resp = api.getCurrentUser()
                val displayName = when {
                    !resp.usuario.isNullOrBlank() -> resp.usuario
                    !resp.username.isNullOrBlank() -> resp.username
                    !resp.nombres.isNullOrBlank() || !resp.apellidos.isNullOrBlank() ->
                        listOfNotNull(resp.nombres?.trim(), resp.apellidos?.trim())
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                    else -> null
                }
                displayName?.let {
                    tvWelcome?.text = "Bienvenido ${it}!"
                }
            } catch (e: Exception) {
                // Si falla, mantenemos el texto por defecto definido en el layout
            }
        }

        // Click en "Comprar Productos" -> navegar a ProductosFragment (lista de productos)
        view.findViewById<View>(R.id.cardComprar)?.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ProductosFragment())
                .addToBackStack(null)
                .commit()
        }

        // Click en "Mis Pedidos" -> navegar al CartFragment (carrito)
        view.findViewById<View>(R.id.cardPedidos)?.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}

