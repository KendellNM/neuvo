package com.example.doloresapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.RecetaDigitalEntity
import com.example.doloresapp.data.remote.api.RecetaDigitalApiService
import com.example.doloresapp.domain.model.RecetaDigital
import com.example.doloresapp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class RecetaDigitalRepository(
    private val apiService: RecetaDigitalApiService,
    private val database: AppDatabase,
    private val context: Context
) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    
    suspend fun procesarReceta(imageUri: Uri, clienteId: Long): Result<RecetaDigital> {
        return try {
            val file = uriToFile(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imagen", file.name, requestFile)
            val clienteIdBody = clienteId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            
            val response = apiService.procesarReceta(imagePart, clienteIdBody)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val receta = response.body()!!.data!!
                
                // Guardar en base de datos local
                saveToLocalDatabase(receta)
                
                Result.Success(receta)
            } else {
                Result.Error(response.body()?.error ?: "Error procesando receta")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido")
        }
    }
    
    suspend fun getRecetasByCliente(clienteId: Long, forceRefresh: Boolean = false): Result<List<RecetaDigital>> {
        return try {
            if (forceRefresh) {
                // Obtener del servidor
                val response = apiService.getRecetasByCliente(clienteId)
                if (response.isSuccessful) {
                    val recetas = response.body() ?: emptyList()
                    // Actualizar base de datos local
                    recetas.forEach { saveToLocalDatabase(it) }
                    Result.Success(recetas)
                } else {
                    Result.Error("Error obteniendo recetas")
                }
            } else {
                // Obtener de base de datos local
                val localRecetas = database.recetaDigitalDao()
                    .getRecetasByCliente(clienteId)
                
                // Convertir Flow a lista
                Result.Success(emptyList()) // Simplificado, en producción usar Flow
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido")
        }
    }
    
    fun getRecetasFlow(clienteId: Long): Flow<List<RecetaDigitalEntity>> {
        return database.recetaDigitalDao().getRecetasByCliente(clienteId)
    }
    
    private suspend fun saveToLocalDatabase(receta: RecetaDigital) {
        val entity = RecetaDigitalEntity(
            id = receta.id,
            clienteId = receta.clienteId,
            imagenUrl = receta.imagenUrl,
            textoExtraido = receta.textoExtraido,
            estado = receta.estado,
            fechaProcesamiento = dateFormat.parse(receta.fechaProcesamiento) ?: Date()
        )
        database.recetaDigitalDao().insertReceta(entity)
    }
    
    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "receta_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        return file
    }
}
