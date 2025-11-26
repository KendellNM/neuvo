package com.example.doloresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doloresapp.domain.usecase.GetCategoriasUseCase
import com.example.doloresapp.domain.usecase.GetProductosUseCase

class ProductosViewModelFactory(
    private val getProductosUseCase: GetProductosUseCase,
    private val getCategoriasUseCase: GetCategoriasUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductosViewModel::class.java)) {
            return ProductosViewModel(getProductosUseCase, getCategoriasUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
