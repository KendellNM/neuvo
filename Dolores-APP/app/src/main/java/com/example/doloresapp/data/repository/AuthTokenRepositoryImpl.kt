
package com.example.doloresapp.data.repository

import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.domain.repository.AuthTokenRepository

class AuthTokenRepositoryImpl : AuthTokenRepository {
    override fun getAuthToken(): String? = TokenStore.getToken()
    override fun clear() = TokenStore.clear()
}