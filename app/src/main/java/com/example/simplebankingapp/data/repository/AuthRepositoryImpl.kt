package com.example.simplebankingapp.data.repository

import ApiClient
import android.content.Context
import com.example.simplebankingapp.data.model.AuthResponseDto
import com.example.simplebankingapp.data.model.ErrorResponse
import com.example.simplebankingapp.data.model.LoginRequest
import com.example.simplebankingapp.data.model.RefreshTokenRequest
import com.example.simplebankingapp.data.model.RefreshTokenResponseDto
import com.example.simplebankingapp.data.model.RegisterRequest
import com.example.simplebankingapp.data.model.RegistrationResponseDto
import com.example.simplebankingapp.data.remote.IAuthService
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.entity.RefreshTokenResponse
import com.example.simplebankingapp.domain.entity.RegistrationResponse
import com.example.simplebankingapp.domain.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

class AuthRepositoryImpl(
    override var context: Context
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
             val iAuthService = ApiClient.getRetrofitInstance(context).create(IAuthService::class.java)

            val response = iAuthService.login(LoginRequest(username, password))
            handleAuthResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<RegistrationResponse> {
        return try {
            val iAuthService = ApiClient.getRetrofitInstance(context).create(IAuthService::class.java)
            val response = iAuthService.register(
                RegisterRequest(username, password, firstName, lastName, phoneNumber)
            )
            handleRegistrationResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    private fun handleAuthResponse(response: Response<AuthResponseDto>): Result<AuthResponse> {
        return if (response.isSuccessful) {
            val authResponse = response.body()?.toAuthResponse()
            if (authResponse != null) {
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Invalid response from server"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val message = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).message
            } catch (e: JsonSyntaxException) {
                errorBody ?: "Unknown error"
            }
            Result.failure(Exception(message))
        }
    }
    private fun handleRefreshResponse(response: Response<RefreshTokenResponseDto>): Result<RefreshTokenResponse> {
        return if (response.isSuccessful) {
            val authResponse = response.body()?.toRefreshTokenResponse()
            if (authResponse != null) {
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Invalid response from server"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val message = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).message
            } catch (e: JsonSyntaxException) {
                errorBody ?: "Unknown error"
            }
            Result.failure(Exception(message))
        }
    }

    private fun handleRegistrationResponse(response: Response<RegistrationResponseDto>): Result<RegistrationResponse> {
        return if (response.isSuccessful) {
            val authResponse = response.body()?.toRegistrationResponse()
            if (authResponse != null) {
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Invalid response from server"))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val message = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).message
            } catch (e: JsonSyntaxException) {
                errorBody ?: "Unknown error"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse> {
        return try {
            val iAuthService = ApiClient.getRetrofitInstance(context).create(IAuthService::class.java)
            val response = iAuthService.refreshToken(RefreshTokenRequest(refreshToken))
            handleRefreshResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}