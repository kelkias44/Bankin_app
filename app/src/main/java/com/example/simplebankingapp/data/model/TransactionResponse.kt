package com.example.simplebankingapp.data.model

import com.example.simplebankingapp.domain.entity.Pageable
import com.example.simplebankingapp.domain.entity.Sort
import java.sql.Timestamp

data class Transaction(
    val id: Int,
    val type: String,
    val amount: Double,
    val relatedAccount: String,
    val direction: String,
    val description: String,
    val timestamp: String,
    val accountId: Int
)

data class TransactionResponse(
    val content: List<Transaction>,
    val pageable: Pageable, // Reusing Pageable from AccountResponse
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort, // Reusing Sort from AccountResponse
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)
