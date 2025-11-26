package com.example.doloresapp.data.remote

import com.example.doloresapp.data.remote.service.ProductoApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = ""  // Ingresar la URL de la API

    val apiService: ProductoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Convertir JSON a objetos Kotlin
            .build()
            .create(ProductoApiService::class.java)
    }
}