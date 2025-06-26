package com.example.simplebankingapp.data.repository

import ApiClient
import android.content.Context
import com.example.simplebankingapp.data.model.ErrorResponse
import com.example.simplebankingapp.data.model.TransactionResponse
import com.example.simplebankingapp.data.remote.ITransactionService
import com.example.simplebankingapp.domain.repository.TransactionRepository
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.model.TransferRequest
import com.example.simplebankingapp.data.model.TransferResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class TransactionRepositoryImpl(
    override val context: Context
) : TransactionRepository {
    override suspend fun getTransactions(accountId: Int, page: Int, size: Int): Result<TransactionResponse> {
        return try {
            val iTransactionService = ApiClient.getRetrofitInstance(context).create(ITransactionService::class.java)
            val response = iTransactionService.getTransactions(accountId, page, size)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error(Exception("Transaction response body is null"))
            } else {
                Result.Error(Exception("Failed to fetch transactions: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun transfer(transferRequest: TransferRequest): Result<TransferResponse> {
        return try {
            val iTransactionService = ApiClient.getRetrofitInstance(context).create(ITransactionService::class.java)
            val response = iTransactionService.transfer(transferRequest)
            if(response.isSuccessful){
                response.body().let {
                    Result.Success(it!!)
                } ?: Result.Error(Exception("Response body is null"))
            }else {
                val errorBody = response.errorBody()?.string()
                val message = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: JsonSyntaxException) {
                    errorBody ?: "Unknown error"
                }
                Result.Error(Exception(message))
            }
        }catch (e: Exception) {
            Result.Error(e)
        }
    }
}