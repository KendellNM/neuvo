package com.example.doloresapp.domain.usecase

import com.example.doloresapp.domain.repository.AuthRepository
class LoginUseCase (private val repository: AuthRepository){
    suspend operator fun invoke(username: String, password: String): String {
        return repository.login(username, password)
    }
}