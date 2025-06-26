package com.example.simplebankingapp.data.model

import com.example.simplebankingapp.domain.entity.RefreshTokenResponse

data class RefreshTokenResponseDto(
    val message: String,
    val accessToken: String,
    val refreshToken: String
    ){
    fun toRefreshTokenResponse(): RefreshTokenResponse{
        return RefreshTokenResponse(
            message, accessToken, refreshToken
        )
    }
}