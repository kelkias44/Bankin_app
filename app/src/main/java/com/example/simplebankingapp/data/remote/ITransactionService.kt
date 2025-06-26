package com.example.simplebankingapp.data.remote

import com.example.simplebankingapp.data.model.TransactionResponse
import com.example.simplebankingapp.data.model.TransferRequest
import com.example.simplebankingapp.data.model.TransferResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ITransactionService {
    @GET("api/transactions/{accountId}")
    suspend fun getTransactions(
        @Path("accountId") accountId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): retrofit2.Response<TransactionResponse>

    @POST("api/accounts/transfer")
    suspend fun transfer(
        @Body transferRequest: TransferRequest
    ): Response<TransferResponse>
}
