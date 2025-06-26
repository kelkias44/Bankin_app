package com.example.simplebankingapp.domain.usecases

import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.model.TransferRequest
import com.example.simplebankingapp.data.model.TransferResponse
import com.example.simplebankingapp.domain.repository.TransactionRepository

class FundTransferUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(transferRequest: TransferRequest): Result<TransferResponse>{
        return transactionRepository.transfer(transferRequest)
    }
}