package com.example.simplebankingapp.data.remote

import com.example.simplebankingapp.data.model.AccountResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IAccountService {
    @GET("api/accounts")
    suspend fun getAccounts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<AccountResponseDto>


}