package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.LoginActivity
import com.example.doloresapp.R
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.UserApi
import com.example.doloresapp.data.sync.SyncManager
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.NetworkUtils
import com.example.doloresapp.utils.RoleManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.home_screen) {
    
    private var bannerOffline: LinearLayout? = null
    private var tvPendingOrders: TextView? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencia al TextView de bienvenida
        val tvWelcome: TextView? = view.findViewById(R.id.tvWelcome)
        
        // Referencias para modo offline
        bannerOffline = view.findViewById(R.id.bannerOffline)
        tvPendingOrders = view.findViewById(R.id.tvPendingOrders)
        
        // Observar estado de conexión
        observeNetworkState()
        
        // Observar pedidos pendientes
        observePendingOrders()

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

        // Botón de cerrar sesión
        view.findViewById<View>(R.id.btnLogout)?.setOnClickListener {
            showLogoutDialog()
        }
    }
    
    private fun observeNetworkState() {
        viewLifecycleOwner.lifecycleScope.launch {
            NetworkUtils.observeNetworkState(requireContext()).collectLatest { isConnected ->
                bannerOffline?.visibility = if (isConnected) View.GONE else View.VISIBLE
            }
        }
    }
    
    private fun observePendingOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            val syncManager = SyncManager.getInstance(requireContext())
            syncManager.pendingCount.collectLatest { count ->
                if (count > 0) {
                    tvPendingOrders?.visibility = View.VISIBLE
                    tvPendingOrders?.text = "• $count pedido(s) pendiente(s)"
                } else {
                    tvPendingOrders?.visibility = View.GONE
                }
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
        // Limpiar token
        TokenStore.clear()
        
        // Limpiar SharedPreferences
        val prefs = requireContext().getSharedPreferences(ApiConstants.Prefs.NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .clear()
            .apply()
        
        // Limpiar rol
        RoleManager.clearUserRole(requireContext())
        
        // Redirigir al login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}

