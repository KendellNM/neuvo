package com.example.doloresapp.data.local.dao

import androidx.room.*
import com.example.doloresapp.data.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos WHERE clienteId = :clienteId ORDER BY fechaCreacion DESC")
    fun getPedidosByCliente(clienteId: Long): Flow<List<PedidoEntity>>
    
    @Query("SELECT * FROM pedidos WHERE id = :id")
    suspend fun getPedidoById(id: Long): PedidoEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedidos(pedidos: List<PedidoEntity>)
    
    @Update
    suspend fun updatePedido(pedido: PedidoEntity)
}
