package com.example.doloresapp.data.remote.api

import com.example.doloresapp.data.remote.dto.CategoriaDTO
import retrofit2.http.GET

interface CategoriaApiService {
    @GET("api/categorias")
    suspend fun getAllCategorias(): List<CategoriaDTO>
}
