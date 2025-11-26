package com.example.doloresapp.data.remote

import com.example.doloresapp.presentation.viewmodel.AuthResponse
import com.example.doloresapp.presentation.viewmodel.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("api/auth/register-cliente")
    suspend fun register(@Body req: RegisterRequest): AuthResponse
}
