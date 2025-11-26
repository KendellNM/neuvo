package com.example.doloresapp.domain.repository

interface AuthTokenRepository {
    fun getAuthToken(): String?
    fun clear()
}