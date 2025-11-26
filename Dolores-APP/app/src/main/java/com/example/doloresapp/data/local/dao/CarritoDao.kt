package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.CarritoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    
    @Query("SELECT * FROM carrito")
    fun getAllItems(): Flow<List<CarritoItem>>
    
    @Query("SELECT * FROM carrito")
    suspend fun getAllItemsList(): List<CarritoItem>
    
    @Query("SELECT SUM(precio * cantidad) FROM carrito")
    fun getTotal(): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM carrito")
    fun getItemCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CarritoItem)
    
    @Update
    suspend fun updateItem(item: CarritoItem)
    
    @Delete
    suspend fun deleteItem(item: CarritoItem)
    
    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun deleteByProductoId(productoId: Long)
    
    @Query("DELETE FROM carrito")
    suspend fun clearCarrito()
    
    @Query("SELECT * FROM carrito WHERE productoId = :productoId")
    suspend fun getItemByProductoId(productoId: Long): CarritoItem?
}
