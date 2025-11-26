package com.example.doloresapp.domain.usecase

import com.example.doloresapp.domain.model.Producto
import com.example.doloresapp.domain.repository.ProductoRepository

class GetProductosUseCase(private val repository: ProductoRepository) {
    suspend fun execute(): List<Producto> {
        return repository.getProductos()  // Obtiene los productos desde el repositorio
    }
}