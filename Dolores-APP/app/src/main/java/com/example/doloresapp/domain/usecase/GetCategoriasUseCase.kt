package com.example.doloresapp.domain.usecase

import com.example.doloresapp.domain.model.Categoria
import com.example.doloresapp.domain.repository.ProductoRepository

class GetCategoriasUseCase(private val repository: ProductoRepository) {
    suspend fun execute(): List<Categoria> {
        return repository.getCategorias()
    }
}