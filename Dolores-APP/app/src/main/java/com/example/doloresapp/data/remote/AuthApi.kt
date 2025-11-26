package com.example.doloresapp.data.remote

import com.example.doloresapp.presentation.viewmodel.AuthRequest
import com.example.doloresapp.data.remote.dto.AuthResponseDto
import retrofit2.http.Body
import retrofit2.http.POST


interface LoginApi {
    @POST("api/auth/login")
    suspend fun login(@Body req: AuthRequest): AuthResponseDto
}