package com.example.simplebankingapp.data.model

data class TransferResponse(
    val fromAccountNumber: String,
    val toAccountNumber: String,
    val amount: Double,
    val message: String
)