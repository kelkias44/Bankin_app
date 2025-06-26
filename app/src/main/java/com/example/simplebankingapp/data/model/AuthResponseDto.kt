package com.example.simplebankingapp.data.model

import com.example.simplebankingapp.domain.entity.AuthResponse

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val username: String,
    val message: String
) {
    fun toAuthResponse(): AuthResponse {
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            username = username,
            message = message
        )
    }
}