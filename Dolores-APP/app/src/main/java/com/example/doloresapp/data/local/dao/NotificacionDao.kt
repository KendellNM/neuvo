package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.NotificacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {
    @Query("SELECT * FROM notificaciones ORDER BY fecha DESC")
    fun getAllNotificaciones(): Flow<List<NotificacionEntity>>
    
    @Query("SELECT * FROM notificaciones WHERE leida = 0 ORDER BY fecha DESC")
    fun getNotificacionesNoLeidas(): Flow<List<NotificacionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificacion(notificacion: NotificacionEntity)
    
    @Update
    suspend fun updateNotificacion(notificacion: NotificacionEntity)
    
    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarComoLeida(id: Long)
}
