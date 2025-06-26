package com.example.simplebankingapp.data.repository

import ApiClient
import android.accounts.NetworkErrorException
import android.content.Context
import com.example.simplebankingapp.data.model.AccountResponseDto
import com.example.simplebankingapp.data.model.ErrorResponse
import com.example.simplebankingapp.data.remote.IAccountService
import com.example.simplebankingapp.domain.entity.AccountResponse
import com.example.simplebankingapp.domain.repository.AccountRepository
import retrofit2.Response
import com.example.simplebankingapp.data.model.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class AccountRepositoryImpl(
    override val context: Context
): AccountRepository {
    override suspend fun getAccounts(page: Int, size: Int): Result<AccountResponse> {
        return try{
            val iAccountService =
                ApiClient.getRetrofitInstance(context).create(IAccountService::class.java)
            val response = iAccountService.getAccounts(page, size)
            handleAccountResponse(response)
        }catch (e: Exception) {
            println("ERROR IS ${e.message}")
            Result.Error(NetworkErrorException("Unable to connect to the server!"))
        }

    }

    private fun handleAccountResponse(response: Response<AccountResponseDto>): Result<AccountResponse> {
        return if (response.isSuccessful) {
            val authResponse = response.body()?.toAccountResponse()
            if (authResponse != null) {
                Result.Success(authResponse)
            } else {
                Result.Error(Exception("Invalid response from server"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val message = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).message
            } catch (e: JsonSyntaxException) {
                errorBody ?: "Unknown error"
            }
            Result.Error(Exception(message))
        }
    }
}