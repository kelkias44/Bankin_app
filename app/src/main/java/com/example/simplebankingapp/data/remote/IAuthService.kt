package com.example.simplebankingapp.data.remote

import com.example.simplebankingapp.data.model.AuthResponseDto
import com.example.simplebankingapp.data.model.LoginRequest
import com.example.simplebankingapp.data.model.RefreshTokenResponseDto
import com.example.simplebankingapp.data.model.RefreshTokenRequest
import com.example.simplebankingapp.data.model.RegisterRequest
import com.example.simplebankingapp.data.model.RegistrationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegistrationResponseDto>

    @POST("api/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponseDto>
}