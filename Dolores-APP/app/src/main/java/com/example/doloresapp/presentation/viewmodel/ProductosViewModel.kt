package com.example.doloresapp.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.doloresapp.domain.model.Categoria
import com.example.doloresapp.domain.model.Producto
import com.example.doloresapp.domain.usecase.GetCategoriasUseCase
import com.example.doloresapp.domain.usecase.GetProductosUseCase
import kotlinx.coroutines.launch

class ProductosViewModel(
    private val getProductosUseCase: GetProductosUseCase,
    private val getCategoriasUseCase: GetCategoriasUseCase
    ) : ViewModel() {

        val productos = MutableLiveData<List<Producto>>()
        val categorias = MutableLiveData<List<Categoria>>()

        fun loadProductos() {
            viewModelScope.launch {
                try {
                    Log.d("ProductosViewModel", "loadProductos(): iniciando consulta de productos...")
                    productos.value = getProductosUseCase.execute()  // Obtener productos
                    Log.d("ProductosViewModel", "loadProductos(): productos cargados -> ${'$'}{productos.value?.size}")
                } catch (e: Exception) {
                    Log.e("ProductosViewModel", "Error cargando productos", e)
                    productos.value = emptyList()
                }
            }
        }

        fun loadCategorias() {
            viewModelScope.launch {
                try {
                    Log.d("ProductosViewModel", "loadCategorias(): iniciando consulta de categorias...")
                    categorias.value = getCategoriasUseCase.execute()  // Obtener categorías
                    Log.d("ProductosViewModel", "loadCategorias(): categorias cargadas -> ${'$'}{categorias.value?.size}")
                } catch (e: Exception) {
                    Log.e("ProductosViewModel", "Error cargando categorías", e)
                    categorias.value = emptyList()
                }
            }
        }
}