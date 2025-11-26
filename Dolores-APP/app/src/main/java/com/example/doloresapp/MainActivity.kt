package com.example.doloresapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.presentation.ui.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa store y verifica sesión
        TokenStore.init(applicationContext)
        // Inicializa el cliente de red compartido (Retrofit + Interceptor)
        NetworkClient.init(applicationContext)
        val token = TokenStore.getToken()
        if (token.isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Redirigir a HomeActivity que maneja los roles correctamente
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}