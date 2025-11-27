package com.example.doloresapp.data.repository

import android.content.Context
import android.util.Log
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.CategoriaEntity
import com.example.doloresapp.data.local.entity.ProductoEntity
import com.example.doloresapp.data.remote.dto.CategoriaDTO
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.domain.model.Categoria
import com.example.doloresapp.domain.model.Producto
import com.example.doloresapp.domain.model.toDomain
import com.example.doloresapp.domain.repository.ProductoRepository
import com.example.doloresapp.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

/**
 * Repositorio de productos con soporte offline
 * - Cuando hay internet: obtiene datos del servidor y los guarda en cache
 * - Cuando no hay internet: usa los datos del cache local
 */
class OfflineProductoRepository(
    private val context: Context,
    private val apiService: ProductoApiService
) : ProductoRepository {
    
    private val database = AppDatabase.getDatabase(context)
    private val productoDao = database.productoDao()
    private val categoriaDao = database.categoriaDao()
    
    companion object {
        private const val TAG = "OfflineProductoRepo"
        private const val CACHE_VALIDITY_MS = 30 * 60 * 1000L // 30 minutos
    }
    
    override suspend fun getProductos(): List<Producto> {
        return if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                Log.d(TAG, "Obteniendo productos del servidor...")
                val productosDto = apiService.getAllProductos()
                
                // Guardar en cache
                val entities = productosDto.map { it.toEntity() }
                productoDao.insertProductos(entities)
                Log.d(TAG, "Guardados ${entities.size} productos en cache")
                
                productosDto.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo del servidor, usando cache: ${e.message}")
                getProductosFromCache()
            }
        } else {
            Log.d(TAG, "Sin conexión, usando cache local")
            getProductosFromCache()
        }
    }
    
    override suspend fun getCategorias(): List<Categoria> {
        return if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                Log.d(TAG, "Obteniendo categorías del servidor...")
                val categoriasDto = apiService.getCategorias()
                
                // Guardar en cache
                val entities = categoriasDto.map { it.toEntity() }
                categoriaDao.insertCategorias(entities)
                Log.d(TAG, "Guardadas ${entities.size} categorías en cache")
                
                categoriasDto.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e(TAG, "Error obteniendo del servidor, usando cache: ${e.message}")
                getCategoriasFromCache()
            }
        } else {
            Log.d(TAG, "Sin conexión, usando cache local")
            getCategoriasFromCache()
        }
    }
    
    override suspend fun searchProductos(query: String): List<Producto> {
        val allProductos = getProductos()
        return allProductos.filter { 
            it.nombre.contains(query, ignoreCase = true) ||
            it.descripcion?.contains(query, ignoreCase = true) == true
        }
    }
    
    suspend fun getProductoById(id: Long): Producto? {
        return if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val dto = apiService.getProductoById(id)
                productoDao.insertProducto(dto.toEntity())
                dto.toDomain()
            } catch (e: Exception) {
                productoDao.getProductoById(id)?.toDomain()
            }
        } else {
            productoDao.getProductoById(id)?.toDomain()
        }
    }
    
    suspend fun getProductosByCategoria(categoriaId: Long): List<Producto> {
        return if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val productosDto = apiService.getProductosByCategoria(categoriaId)
                productosDto.map { it.toDomain() }
            } catch (e: Exception) {
                getProductos().filter { it.categoriaId == categoriaId }
            }
        } else {
            getProductos().filter { it.categoriaId == categoriaId }
        }
    }
    
    private suspend fun getProductosFromCache(): List<Producto> {
        val entities = productoDao.getAllProductosList()
        Log.d(TAG, "Productos en cache: ${entities.size}")
        return entities.map { it.toDomain() }
    }
    
    private suspend fun getCategoriasFromCache(): List<Categoria> {
        val entities = categoriaDao.getAllCategoriasList()
        Log.d(TAG, "Categorías en cache: ${entities.size}")
        return entities.map { it.toDomain() }
    }
    
    fun getProductosFlow(): Flow<List<Producto>> {
        return productoDao.getAllProductos().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getCategoriasFlow(): Flow<List<Categoria>> {
        return categoriaDao.getAllCategorias().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun refreshCache() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val productosDto = apiService.getAllProductos()
                val entities = productosDto.map { it.toEntity() }
                productoDao.deleteAll()
                productoDao.insertProductos(entities)
                
                val categoriasDto = apiService.getCategorias()
                val catEntities = categoriasDto.map { it.toEntity() }
                categoriaDao.deleteAll()
                categoriaDao.insertCategorias(catEntities)
                
                Log.d(TAG, "Cache actualizado: ${entities.size} productos, ${catEntities.size} categorías")
            } catch (e: Exception) {
                Log.e(TAG, "Error actualizando cache: ${e.message}")
            }
        }
    }
    
    fun isOffline(): Boolean = !NetworkUtils.isNetworkAvailable(context)
}

// Extensiones para convertir DTOs a Entities
private fun ProductoDTO.toEntity() = ProductoEntity(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
    precio = this.precio,
    stock = this.stock,
    disponible = this.stock > 0,
    imagenUrl = this.imagen_url,
    laboratorioNombre = null,
    categoria = this.categoria?.nombre,
    requiereReceta = this.requerireReceta ?: false,
    lastUpdated = Date()
)

private fun CategoriaDTO.toEntity() = CategoriaEntity(
    id = this.id,
    nombre = this.nombre,
    lastUpdated = Date()
)

// Extensiones para convertir Entities a Domain
private fun ProductoEntity.toDomain() = Producto(
    id = this.id,
    nombre = this.nombre,
    descripcion = this.descripcion,
    precio = this.precio,
    concentracion = null,
    precioOferta = null,
    imagenUrl = this.imagenUrl,
    stock = this.stock,
    categoriaId = null
)

private fun CategoriaEntity.toDomain() = Categoria(
    id = this.id,
    nombre = this.nombre
)
