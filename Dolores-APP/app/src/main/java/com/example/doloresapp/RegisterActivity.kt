package com.example.doloresapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.RegisterApi
import com.example.doloresapp.presentation.viewmodel.AuthResponse
import com.example.doloresapp.presentation.viewmodel.RegisterRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Datos del paso 1
    private var nombres: String = ""
    private var apellidos: String = ""
    private var genero: String = ""
    private var dni: String = ""
    private var telefono: String = ""
    private var fechaNacimiento: String = "1990-01-01" // Valor por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)

        // Habilita edge-to-edge para ocupar toda la pantalla respetando los system bars
        enableEdgeToEdge()

        showStep1()
    }

    private fun showStep1() {
        setContentView(R.layout.register_1_layout)
        // Aplica padding según los system bars al root del paso 1
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root_step1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameInput: TextInputEditText = findViewById(R.id.name)
        val lastnameInput: TextInputEditText = findViewById(R.id.lastname)
        val genderSelector: android.widget.AutoCompleteTextView = findViewById(R.id.gender_selector)
        val dniInput: TextInputEditText = findViewById(R.id.dni)
        val phoneInput: TextInputEditText = findViewById(R.id.phone)
        val birthdateInput: TextInputEditText = findViewById(R.id.birthdate_input)
        val nextButton: MaterialButton = findViewById(R.id.acceder_button)
        val loginLink: TextView = findViewById(R.id.login_link)

        // Setup gender dropdown if needed (array already in XML via entries)
        // Optionally we could re-attach adapter programmatically
        val genders = resources.getStringArray(R.array.gender_options)
        genderSelector.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders))

        // Abrir DatePicker al tocar el campo de fecha
        val openDatePicker = {
            val today = java.util.Calendar.getInstance()
            val year = today.get(java.util.Calendar.YEAR)
            val month = today.get(java.util.Calendar.MONTH)
            val day = today.get(java.util.Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                // Formato AAAA-MM-DD con relleno de ceros
                val mm = String.format("%02d", m + 1)
                val dd = String.format("%02d", d)
                val formatted = "$y-$mm-$dd"
                birthdateInput.setText(formatted)
                fechaNacimiento = formatted
            }, year, month, day).show()
        }
        birthdateInput.setOnClickListener { openDatePicker.invoke() }
        birthdateInput.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) openDatePicker.invoke() }

        nextButton.setOnClickListener {
            val name = nameInput.text?.toString()?.trim().orEmpty()
            val lastname = lastnameInput.text?.toString()?.trim().orEmpty()
            val gen = genderSelector.text?.toString()?.trim().orEmpty()
            val dniVal = dniInput.text?.toString()?.trim().orEmpty()
            val phoneVal = phoneInput.text?.toString()?.trim().orEmpty()
            val birthVal = birthdateInput.text?.toString()?.trim().orEmpty()

            // Validaciones
            var hasError = false
            if (name.isEmpty()) { nameInput.error = "Nombre es obligatorio"; hasError = true }
            if (lastname.isEmpty()) { lastnameInput.error = "Apellidos son obligatorios"; hasError = true }
            if (gen.isEmpty()) { genderSelector.error = "Seleccione un género"; hasError = true }
            if (dniVal.isEmpty()) { dniInput.error = "DNI es obligatorio"; hasError = true }
            if (dniVal.length != 8) { dniInput.error = "DNI debe tener 8 dígitos"; hasError = true }
            if (phoneVal.isEmpty()) { phoneInput.error = "Teléfono es obligatorio"; hasError = true }
            if (phoneVal.length != 9) { phoneInput.error = "Teléfono debe tener 9 dígitos"; hasError = true }
            if (birthVal.isEmpty()) { birthdateInput.error = "Fecha de nacimiento es obligatoria"; hasError = true }

            if (hasError) return@setOnClickListener

            // Guardar datos e ir al paso 2
            nombres = name
            apellidos = lastname
            genero = when(gen.lowercase()) {
                "masculino" -> "M"
                "femenino" -> "F"
                else -> "M" // Valor por defecto
            }
            dni = dniVal
            telefono = phoneVal
            fechaNacimiento = birthVal
            showStep2()
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showStep2() {
        setContentView(R.layout.register_2_layout)
        // Aplica padding según los system bars al root del paso 2
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root_step2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput: TextInputEditText = findViewById(R.id.email_input)
        val passwordInput: TextInputEditText = findViewById(R.id.password_input)
        // Asegurarse de que el ID coincida exactamente con el definido en el layout
        val confirmPasswordInput: TextInputEditText = findViewById(com.example.doloresapp.R.id.confirm_password_input)
        val registerButton: MaterialButton = findViewById(R.id.acceder_button)
        val loginLink: TextView = findViewById(R.id.login_link)

        registerButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString().orEmpty()
            val confirm = confirmPasswordInput.text?.toString().orEmpty()

            // Validations
            if (email.isEmpty()) {
                emailInput.error = "Correo es obligatorio"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Correo no válido"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordInput.error = "Contraseña es obligatoria"
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordInput.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }
            if (password != confirm) {
                confirmPasswordInput.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            registerButton.isEnabled = false

            val api = NetworkClient.createService(RegisterApi::class.java)
            lifecycleScope.launch {
                try {
                            // Crear nombre de usuario a partir del correo (primera parte antes de @)
                    val usuario = email.substringBefore("@")
                    
                    val req = RegisterRequest(
                        usuario = usuario,
                        correo = email,
                        password = password,
                        nombres = nombres,
                        apellidos = apellidos,
                        dni = dni,
                        telefono = telefono,
                        genero = genero,
                        fechaNacimiento = fechaNacimiento
                    )
                    val resp: AuthResponse = api.register(req)
                    TokenStore.saveToken(resp.token)
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, e.message ?: "Error de registro", Toast.LENGTH_LONG).show()
                } finally {
                    registerButton.isEnabled = true
                }
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
