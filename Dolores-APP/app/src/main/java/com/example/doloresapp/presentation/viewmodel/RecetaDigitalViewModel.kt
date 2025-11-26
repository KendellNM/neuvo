package com.example.doloresapp.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doloresapp.data.repository.RecetaDigitalRepository
import com.example.doloresapp.domain.model.RecetaDigital
import com.example.doloresapp.utils.Result
import kotlinx.coroutines.launch

class RecetaDigitalViewModel(
    private val repository: RecetaDigitalRepository
) : ViewModel() {
    
    private val _procesarRecetaState = MutableLiveData<Result<RecetaDigital>>()
    val procesarRecetaState: LiveData<Result<RecetaDigital>> = _procesarRecetaState
    
    private val _recetasState = MutableLiveData<Result<List<RecetaDigital>>>()
    val recetasState: LiveData<Result<List<RecetaDigital>>> = _recetasState
    
    fun procesarReceta(imageUri: Uri, clienteId: Long) {
        viewModelScope.launch {
            _procesarRecetaState.value = Result.Loading
            val result = repository.procesarReceta(imageUri, clienteId)
            _procesarRecetaState.value = result
        }
    }
    
    fun getRecetasByCliente(clienteId: Long, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _recetasState.value = Result.Loading
            val result = repository.getRecetasByCliente(clienteId, forceRefresh)
            _recetasState.value = result
        }
    }
}
