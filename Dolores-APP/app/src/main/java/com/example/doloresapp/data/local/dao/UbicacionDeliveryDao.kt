package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.UbicacionDeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UbicacionDeliveryDao {
    @Query("SELECT * FROM ubicaciones_delivery WHERE pedidoId = :pedidoId ORDER BY timestamp DESC")
    fun getUbicacionesByPedido(pedidoId: Long): Flow<List<UbicacionDeliveryEntity>>
    
    @Query("SELECT * FROM ubicaciones_delivery WHERE pedidoId = :pedidoId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getUltimaUbicacion(pedidoId: Long): UbicacionDeliveryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUbicacion(ubicacion: UbicacionDeliveryEntity)
}
