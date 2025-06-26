package com.example.simplebankingapp.domain.usecases

import android.content.Context
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<AuthResponse> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Username and password cannot be empty"))
        }
        return authRepository.login(username, password)
    }
}