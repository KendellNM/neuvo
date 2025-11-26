package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<ProductoEntity>>
    
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): ProductoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<ProductoEntity>)
    
    @Delete
    suspend fun deleteProducto(producto: ProductoEntity)
    
    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}
