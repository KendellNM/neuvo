package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.RecetaDigitalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDigitalDao {
    @Query("SELECT * FROM recetas_digitales WHERE clienteId = :clienteId ORDER BY fechaProcesamiento DESC")
    fun getRecetasByCliente(clienteId: Long): Flow<List<RecetaDigitalEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceta(receta: RecetaDigitalEntity)
}
