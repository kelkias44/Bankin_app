package com.example.simplebankingapp.domain.entity

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val username: String,
    val message: String
)
