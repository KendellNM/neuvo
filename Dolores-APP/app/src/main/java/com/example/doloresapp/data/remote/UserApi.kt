package com.example.doloresapp.data.remote

import com.example.doloresapp.presentation.viewmodel.UsuarioResponse
import retrofit2.http.GET

interface UserApi {
    // Nota: BASE_URL ya incluye "/api/" (ver Constants.BASE_URL)
    // Por lo tanto, este endpoint resultará en http://host/api/usuario/me
    @GET("usuario/me")
    suspend fun getCurrentUser(): UsuarioResponse
}
