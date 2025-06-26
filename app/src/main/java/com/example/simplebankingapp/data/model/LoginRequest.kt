package com.example.simplebankingapp.data.model

data class LoginRequest(
    val username: String,
    val passwordHash: String
)
