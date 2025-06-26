package com.example.simplebankingapp.domain.entity

data class RegistrationResponse(
    val message: String,
    val username : String,
    val userId : Int,
    val initialAccountNumber: String
)
