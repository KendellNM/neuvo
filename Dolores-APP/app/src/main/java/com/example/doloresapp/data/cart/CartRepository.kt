package com.example.doloresapp.data.cart

import android.content.Context
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.CarritoItem
import com.example.doloresapp.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Repositorio del carrito usando SQLite (Room) para persistencia
 */
object CartRepository {
    private var database: AppDatabase? = null
    
    fun init(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }
    
    private val carritoDao get() = database?.carritoDao()
    
    // Versión síncrona para compatibilidad
    @Synchronized
    fun add(producto: Producto, quantity: Int = 1) {
        runBlocking {
            addSuspend(producto, quantity)
        }
    }
    
    suspend fun addSuspend(producto: Producto, quantity: Int = 1) {
        val dao = carritoDao ?: return
        val existing = dao.getItemByProductoId(producto.id)
        if (existing != null) {
            dao.updateItem(existing.copy(cantidad = existing.cantidad + quantity))
        } else {
            dao.insertItem(
                CarritoItem(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    cantidad = quantity,
                    imagenUrl = producto.imagenUrl
                )
            )
        }
    }
    
    @Synchronized
    fun remove(productId: Long, quantity: Int = 1) {
        runBlocking {
            removeSuspend(productId, quantity)
        }
    }
    
    suspend fun removeSuspend(productId: Long, quantity: Int = 1) {
        val dao = carritoDao ?: return
        val existing = dao.getItemByProductoId(productId) ?: return
        val newQuantity = existing.cantidad - quantity
        if (newQuantity <= 0) {
            dao.deleteByProductoId(productId)
        } else {
            dao.updateItem(existing.copy(cantidad = newQuantity))
        }
    }
    
    @Synchronized
    fun clear() {
        runBlocking {
            clearSuspend()
        }
    }
    
    suspend fun clearSuspend() {
        carritoDao?.clearCarrito()
    }
    
    @Synchronized
    fun getItems(): List<Pair<Producto, Int>> {
        return runBlocking {
            getItemsSuspend()
        }
    }
    
    suspend fun getItemsSuspend(): List<Pair<Producto, Int>> {
        val items = carritoDao?.getAllItemsList() ?: emptyList()
        return items.map { item ->
            Producto(
                id = item.productoId,
                nombre = item.nombre,
                descripcion = null,
                precio = item.precio,
                concentracion = null,
                precioOferta = null,
                imagenUrl = item.imagenUrl,
                stock = 0,
                categoriaId = null
            ) to item.cantidad
        }
    }
    
    @Synchronized
    fun getCount(): Int {
        return runBlocking {
            getCountSuspend()
        }
    }
    
    suspend fun getCountSuspend(): Int {
        return carritoDao?.getAllItemsList()?.sumOf { it.cantidad } ?: 0
    }
    
    // Flow para observar cambios en tiempo real
    fun getItemsFlow(): Flow<List<Pair<Producto, Int>>>? {
        return carritoDao?.getAllItems()?.map { items ->
            items.map { item ->
                Producto(
                    id = item.productoId,
                    nombre = item.nombre,
                    descripcion = null,
                    precio = item.precio,
                    concentracion = null,
                    precioOferta = null,
                    imagenUrl = item.imagenUrl,
                    stock = 0,
                    categoriaId = null
                ) to item.cantidad
            }
        }
    }
    
    fun getTotalFlow(): Flow<Double?>? = carritoDao?.getTotal()
    
    fun getCountFlow(): Flow<Int>? = carritoDao?.getItemCount()
}
