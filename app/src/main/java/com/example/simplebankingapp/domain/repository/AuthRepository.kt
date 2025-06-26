package com.example.simplebankingapp.domain.repository

import android.content.Context
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.entity.RefreshTokenResponse
import com.example.simplebankingapp.domain.entity.RegistrationResponse

interface AuthRepository {
    var context: Context
    suspend fun login(username: String, password: String): Result<AuthResponse>
    suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<RegistrationResponse>

    suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse>
}