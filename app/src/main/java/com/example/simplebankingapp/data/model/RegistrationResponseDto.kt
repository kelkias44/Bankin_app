package com.example.simplebankingapp.data.model

import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.entity.RegistrationResponse

data class RegistrationResponseDto(
    val message: String,
    val username: String,
    val userId: Int,
    val initialAccountNumber: String
) {
    fun toRegistrationResponse(): RegistrationResponse {
        return RegistrationResponse(
            message,
            username,
            userId,
            initialAccountNumber
        )
    }
}

