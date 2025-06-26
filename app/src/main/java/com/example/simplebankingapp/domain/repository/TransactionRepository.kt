package com.example.simplebankingapp.domain.repository

import android.content.Context
import com.example.simplebankingapp.data.model.TransactionResponse
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.model.TransferRequest
import com.example.simplebankingapp.data.model.TransferResponse

interface TransactionRepository {
    val context:Context
    suspend fun getTransactions(accountId: Int, page: Int, size: Int): Result<TransactionResponse>
    suspend fun transfer(transferRequest: TransferRequest): Result<TransferResponse>
}