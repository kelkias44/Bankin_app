package com.example.simplebankingapp.domain.usecases

import com.example.simplebankingapp.data.model.TransactionResponse
import com.example.simplebankingapp.domain.repository.TransactionRepository
import com.example.simplebankingapp.data.model.Result

class GetTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(accountId: Int, page: Int, size: Int): Result<TransactionResponse> {
        return transactionRepository.getTransactions(accountId, page, size)
    }
}