package com.example.simplebankingapp.domain.usecases

import com.example.simplebankingapp.domain.entity.AccountResponse
import com.example.simplebankingapp.domain.repository.AccountRepository
import com.example.simplebankingapp.data.model.Result

class GetAccountsUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(page: Int, size: Int): Result<AccountResponse> {
        return accountRepository.getAccounts(page, size)
    }
}