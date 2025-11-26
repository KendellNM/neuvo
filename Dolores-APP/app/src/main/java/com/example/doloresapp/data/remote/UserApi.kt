package com.example.doloresapp.data.remote

import com.example.doloresapp.presentation.viewmodel.UsuarioResponse
import retrofit2.http.GET

interface UserApi {
    @GET("api/usuarios/me")
    suspend fun getCurrentUser(): UsuarioResponse
}
