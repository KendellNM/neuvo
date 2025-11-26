package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.api.QRApiService
import com.example.doloresapp.di.ServiceLocator
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

class QRScannerActivity : AppCompatActivity() {
    
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var qrApiService: QRApiService
    private var isScanning = true
    
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)
        
        barcodeView = findViewById(R.id.barcode_scanner)
        qrApiService = ServiceLocator.provideQRApiService()
        
        if (checkCameraPermission()) {
            startScanning()
        } else {
            requestCameraPermission()
        }
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
            CAMERA_PERMISSION_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun startScanning() {
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (isScanning && result != null) {
                    isScanning = false
                    handleQRCode(result.text)
                }
            }
            
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
    }
    
    private fun handleQRCode(qrCode: String) {
        try {
            val productoId = qrCode.toLong()
            fetchProducto(productoId)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show()
            isScanning = true
        }
    }
    
    private fun fetchProducto(productoId: Long) {
        lifecycleScope.launch {
            try {
                val response = qrApiService.getProductoByQR(productoId)
                if (response.isSuccessful && response.body() != null) {
                    val producto = response.body()!!
                    
                    // Retornar resultado
                    val intent = Intent().apply {
                        putExtra("producto_id", producto.id)
                        putExtra("producto_nombre", producto.nombre)
                        putExtra("producto_precio", producto.precio)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@QRScannerActivity,
                        "Producto no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                    isScanning = true
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@QRScannerActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                isScanning = true
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }
    
    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}
