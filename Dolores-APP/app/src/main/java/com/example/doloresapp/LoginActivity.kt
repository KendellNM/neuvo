package com.example.doloresapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.LoginApi
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.presentation.viewmodel.AuthRequest
import com.example.doloresapp.presentation.ui.HomeActivity
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.RoleManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa almacenamiento de token y NetworkClient SIEMPRE
        TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)
        
        // Navega si ya hay sesión activa
        TokenStore.getToken()?.let { existing ->
            if (existing.isNotBlank()) {
                // Redirigir a HomeActivity (maneja roles)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
                return
            }
        }

        // Habilita edge-to-edge para ocupar toda la pantalla
        enableEdgeToEdge()
        setContentView(R.layout.login_layout)

        // Aplica padding según los system bars (status/navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Animaciones sutiles: círculos con parallax y entrada suave de elementos
        val interp = FastOutSlowInInterpolator()
        fun dp(v: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics)

        val circleBack: View? = findViewById(R.id.login_circle_back)
        val circleFront: View? = findViewById(R.id.login_circle_front)
        val logo: View? = findViewById(R.id.imageView2)
        val title: View? = findViewById(R.id.login_title)
        val emailLayout: View? = findViewById(R.id.name_input_layout)
        val passLayout: View? = findViewById(R.id.lastname_input_layout)
        val submit: View? = findViewById(R.id.acceder_button)
        val msg: View? = findViewById(R.id.login_message)
        val link: View? = findViewById(R.id.login_link)

        // Estados iniciales
        listOfNotNull(logo, title, emailLayout, passLayout, submit, msg, link).forEach { view ->
            view.alpha = 0f
            view.translationY = dp(18f)
        }
        circleBack?.apply { alpha = 0f; translationY = -dp(12f) }
        circleFront?.apply { alpha = 0f; translationY = -dp(20f) }

        // Parallax círculos
        circleBack?.animate()?.alpha(1f)?.translationY(0f)?.setDuration(500L)?.setInterpolator(interp)?.start()
        circleFront?.animate()?.alpha(1f)?.translationY(0f)?.setDuration(600L)?.setStartDelay(60L)?.setInterpolator(interp)?.start()

        // Secuencia elementos
        val sequence = listOfNotNull(logo, title, emailLayout, passLayout, submit, msg, link)
        val baseDuration = 320L
        val step = 60L
        sequence.forEachIndexed { i, v ->
            v.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(baseDuration)
                .setStartDelay(140L + i * step)
                .setInterpolator(interp)
                .start()
        }

        val emailInput: TextInputEditText = findViewById(R.id.email_input)
        val passwordInput: TextInputEditText = findViewById(R.id.password_input)
        val loginButton: com.google.android.material.button.MaterialButton = findViewById(R.id.acceder_button)
        val registerLink: TextView = findViewById(R.id.login_link)

        val api = NetworkClient.createService(LoginApi::class.java)

        loginButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString().orEmpty()

            if (!isValidEmail(email)) {
                emailInput.error = "Correo inválido"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordInput.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }

            loginButton.isEnabled = false
            lifecycleScope.launch {
                try {
                    val resp = api.login(AuthRequest(username = email, password = password))
                    
                    // Guardar token
                    TokenStore.saveToken(resp.token)
                    
                    // NUEVO: Guardar rol del usuario
                    val userRole = RoleManager.parseRoleFromBackend(resp.roles ?: emptyList())
                    RoleManager.saveUserRole(this@LoginActivity, userRole)
                    
                    // Guardar información del usuario
                    val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
                    prefs.edit()
                        .putString(ApiConstants.Prefs.USER_EMAIL, email)
                        .putBoolean(ApiConstants.Prefs.IS_LOGGED_IN, true)
                        .apply()
                    
                    // Obtener y guardar IDs específicos del usuario (clienteId, repartidorId)
                    try {
                        val userApi = NetworkClient.createService(com.example.doloresapp.data.remote.UserApi::class.java)
                        val currentUser = userApi.getCurrentUser()
                        prefs.edit()
                            .putLong(ApiConstants.Prefs.USER_ID, currentUser.id ?: 0L)
                            .putLong("cliente_id", currentUser.clienteId ?: 0L)
                            .putLong("repartidor_id", currentUser.repartidorId ?: 0L)
                            .apply()
                    } catch (e: Exception) {
                        android.util.Log.e("Login", "Error obteniendo datos de usuario: ${e.message}")
                    }
                    
                    Toast.makeText(this@LoginActivity, "Login exitoso como ${userRole.name}", Toast.LENGTH_SHORT).show()
                    
                    // Redirigir a HomeActivity (maneja roles automáticamente)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, e.message ?: "Error de autenticación", Toast.LENGTH_LONG).show()
                } finally {
                    loginButton.isEnabled = true
                }
            }
        }

        // Ir a la pantalla de registro
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean =
        email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
