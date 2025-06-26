package com.example.simplebankingapp.domain.entity

data class User(
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null
)
