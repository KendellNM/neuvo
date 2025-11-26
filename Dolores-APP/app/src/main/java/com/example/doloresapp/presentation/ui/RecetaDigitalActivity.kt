package com.example.doloresapp.presentation.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.repository.RecetaDigitalRepository
import com.example.doloresapp.di.ServiceLocator
import com.example.doloresapp.presentation.viewmodel.RecetaDigitalViewModel
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.Result
import java.io.File

class RecetaDigitalActivity : AppCompatActivity() {
    
    private lateinit var viewModel: RecetaDigitalViewModel
    private lateinit var imageView: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnProcesar: Button
    private lateinit var tvResultado: TextView
    private lateinit var progressBar: ProgressBar
    
    private var selectedImageUri: Uri? = null
    private var clienteId: Long = 0
    private var photoFile: File? = null
    
    companion object {
        private const val REQUEST_CAMERA = 1001
        private const val REQUEST_GALLERY = 1002
        private const val REQUEST_CAMERA_PERMISSION = 1003
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receta_digital)
        
        // Obtener clienteId de SharedPreferences
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        clienteId = prefs.getLong(ApiConstants.Prefs.USER_ID, 0)
        
        if (clienteId == 0L) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        initViews()
        initViewModel()
        setupListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        imageView = findViewById(R.id.iv_receta)
        btnCamera = findViewById(R.id.btn_camera)
        btnGallery = findViewById(R.id.btn_gallery)
        btnProcesar = findViewById(R.id.btn_procesar)
        tvResultado = findViewById(R.id.tv_resultado)
        progressBar = findViewById(R.id.progress_bar)
        
        btnProcesar.isEnabled = false
    }
    
    private fun initViewModel() {
        val database = AppDatabase.getDatabase(this)
        val apiService = ServiceLocator.provideRecetaApiService()
        val repository = RecetaDigitalRepository(apiService, database, this)
        viewModel = RecetaDigitalViewModel(repository)
    }
    
    private fun setupListeners() {
        btnCamera.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
        
        btnGallery.setOnClickListener {
            openGallery()
        }
        
        btnProcesar.setOnClickListener {
            selectedImageUri?.let { uri ->
                viewModel.procesarReceta(uri, clienteId)
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.procesarRecetaState.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = android.view.View.VISIBLE
                    btnProcesar.isEnabled = false
                    tvResultado.text = "Procesando receta con OCR..."
                }
                is Result.Success -> {
                    progressBar.visibility = android.view.View.GONE
                    btnProcesar.isEnabled = true
                    
                    val receta = result.data
                    tvResultado.text = buildString {
                        append("✅ Receta procesada exitosamente\n\n")
                        append("Estado: ${receta.estado}\n\n")
                        append("Texto extraído:\n${receta.textoExtraido}\n\n")
                        
                        if (!receta.detalles.isNullOrEmpty()) {
                            append("Medicamentos detectados:\n")
                            receta.detalles.forEach { detalle ->
                                append("• ${detalle.medicamentoTexto}")
                                if (detalle.validado) {
                                    append(" ✓ (${detalle.productoNombre})")
                                }
                                append("\n")
                            }
                        }
                    }
                    
                    Toast.makeText(this, "Receta procesada correctamente", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    progressBar.visibility = android.view.View.GONE
                    btnProcesar.isEnabled = true
                    tvResultado.text = "❌ Error: ${result.message}"
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }
    
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = File(cacheDir, "receta_${System.currentTimeMillis()}.jpg")
        
        val photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile!!
        )
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    photoFile?.let { file ->
                        selectedImageUri = Uri.fromFile(file)
                        imageView.setImageURI(selectedImageUri)
                        btnProcesar.isEnabled = true
                        tvResultado.text = "Imagen capturada. Presiona 'Procesar' para extraer texto."
                    }
                }
                REQUEST_GALLERY -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        imageView.setImageURI(uri)
                        btnProcesar.isEnabled = true
                        tvResultado.text = "Imagen seleccionada. Presiona 'Procesar' para extraer texto."
                    }
                }
            }
        }
    }
}
