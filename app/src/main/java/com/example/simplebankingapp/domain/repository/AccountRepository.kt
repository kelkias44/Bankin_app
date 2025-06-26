package com.example.simplebankingapp.domain.repository

import android.content.Context
import com.example.simplebankingapp.domain.entity.AccountResponse
import com.example.simplebankingapp.data.model.Result

interface AccountRepository {
    val context:Context
    suspend fun getAccounts(page: Int, size: Int): Result<AccountResponse>
}