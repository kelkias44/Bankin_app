package com.example.simplebankingapp.domain.entity

data class RefreshTokenResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    )
