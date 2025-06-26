package com.example.simplebankingapp.data.model

data class RegisterRequest(
    val username: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
