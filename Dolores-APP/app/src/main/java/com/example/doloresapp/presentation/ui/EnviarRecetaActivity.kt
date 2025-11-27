package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.RecetasDigitalesApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EnviarRecetaActivity : AppCompatActivity() {

    private lateinit var ivReceta: ImageView
    private lateinit var placeholderContainer: View
    private lateinit var btnCamara: MaterialButton
    private lateinit var btnGaleria: MaterialButton
    private lateinit var btnObtenerUbicacion: MaterialButton
    private lateinit var btnEnviarReceta: MaterialButton
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etObservaciones: TextInputEditText
    private lateinit var tvCoordenadas: TextView
    private lateinit var ubicacionConfirmada: View
    private lateinit var tvEstado: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private var latitud: Double? = null
    private var longitud: Double? = null

    private val recetasApi by lazy {
        NetworkClient.createService(RecetasDigitalesApi::class.java)
    }

    // Launcher para cámara
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoPath != null) {
            selectedImageUri = Uri.fromFile(File(currentPhotoPath!!))
            ivReceta.setImageURI(selectedImageUri)
            placeholderContainer.visibility = View.GONE
        }
    }

    // Launcher para galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            ivReceta.setImageURI(it)
            placeholderContainer.visibility = View.GONE
        }
    }

    // Launcher para permisos de ubicación
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            obtenerUbicacion()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enviar_receta)

        NetworkClient.init(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        ivReceta = findViewById(R.id.ivReceta)
        placeholderContainer = findViewById(R.id.placeholderContainer)
        btnCamara = findViewById(R.id.btnCamara)
        btnGaleria = findViewById(R.id.btnGaleria)
        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbicacion)
        btnEnviarReceta = findViewById(R.id.btnEnviarReceta)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        etObservaciones = findViewById(R.id.etObservaciones)
        tvCoordenadas = findViewById(R.id.tvCoordenadas)
        ubicacionConfirmada = findViewById(R.id.ubicacionConfirmada)
        tvEstado = findViewById(R.id.tvEstado)
        progressBar = findViewById(R.id.progressBar)

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        btnCamara.setOnClickListener { abrirCamara() }
        btnGaleria.setOnClickListener { abrirGaleria() }
        btnObtenerUbicacion.setOnClickListener { solicitarUbicacion() }
        btnEnviarReceta.setOnClickListener { enviarReceta() }
    }

    private fun abrirCamara() {
        val photoFile = createImageFile()
        photoFile?.let {
            val photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            takePictureLauncher.launch(photoUri)
        }
    }

    private fun abrirGaleria() {
        pickImageLauncher.launch("image/*")
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("RECETA_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun solicitarUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            obtenerUbicacion()
        }
    }

    private fun obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitud = it.latitude
                longitud = it.longitude
                ubicacionConfirmada.visibility = View.VISIBLE
                tvCoordenadas.text = "Ubicación obtenida correctamente"
                btnObtenerUbicacion.text = "✓ Ubicación confirmada"
                btnObtenerUbicacion.isEnabled = false
                Toast.makeText(this, "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "No se pudo obtener la ubicación. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarReceta() {
        // Validaciones
        if (selectedImageUri == null) {
            Toast.makeText(this, "Selecciona una imagen de la receta", Toast.LENGTH_SHORT).show()
            return
        }

        val direccion = etDireccion.text.toString().trim()
        if (direccion.isEmpty()) {
            etDireccion.error = "Ingresa la dirección de entrega"
            return
        }

        if (latitud == null || longitud == null) {
            Toast.makeText(this, "Obtén tu ubicación primero", Toast.LENGTH_SHORT).show()
            return
        }

        val telefono = etTelefono.text.toString().trim()
        val observaciones = etObservaciones.text.toString().trim()

        // Mostrar progreso
        progressBar.visibility = View.VISIBLE
        btnEnviarReceta.isEnabled = false
        tvEstado.visibility = View.VISIBLE
        tvEstado.text = "Enviando receta..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageFile = getFileFromUri(selectedImageUri!!)
                if (imageFile == null) {
                    withContext(Dispatchers.Main) {
                        mostrarError("Error al procesar la imagen")
                    }
                    return@launch
                }

                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("imagen", imageFile.name, requestFile)

                val direccionBody = direccion.toRequestBody("text/plain".toMediaTypeOrNull())
                val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val telefonoBody = if (telefono.isNotEmpty()) 
                    telefono.toRequestBody("text/plain".toMediaTypeOrNull()) else null
                val observacionesBody = if (observaciones.isNotEmpty()) 
                    observaciones.toRequestBody("text/plain".toMediaTypeOrNull()) else null

                val response = recetasApi.enviarRecetaConUbicacion(
                    imagen = imagePart,
                    direccionEntrega = direccionBody,
                    latitud = latitudBody,
                    longitud = longitudBody,
                    telefonoContacto = telefonoBody,
                    observaciones = observacionesBody
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnEnviarReceta.isEnabled = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        tvEstado.text = "✅ Receta enviada exitosamente!\nUn farmacéutico la revisará pronto."
                        tvEstado.setTextColor(getColor(android.R.color.holo_green_dark))
                        
                        Toast.makeText(
                            this@EnviarRecetaActivity,
                            "Receta enviada correctamente",
                            Toast.LENGTH_LONG
                        ).show()

                        // Limpiar formulario después de 2 segundos
                        btnEnviarReceta.postDelayed({
                            finish()
                        }, 2000)
                    } else {
                        mostrarError(response.body()?.message ?: "Error al enviar la receta")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarError("Error: ${e.message}")
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        progressBar.visibility = View.GONE
        btnEnviarReceta.isEnabled = true
        tvEstado.text = "❌ $mensaje"
        tvEstado.setTextColor(getColor(android.R.color.holo_red_dark))
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            // Si es un archivo local
            if (uri.scheme == "file") {
                File(uri.path!!)
            } else {
                // Si es content URI, copiar a archivo temporal
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File.createTempFile("receta_", ".jpg", cacheDir)
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile
            }
        } catch (e: Exception) {
            null
        }
    }
}
