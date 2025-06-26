package com.example.simplebankingapp.data.model

import com.example.simplebankingapp.domain.entity.Account
import com.example.simplebankingapp.domain.entity.AccountResponse
import com.example.simplebankingapp.domain.entity.Pageable
import com.example.simplebankingapp.domain.entity.Sort

data class AccountDto(
    val id: Int,
    val accountNumber: String,
    val balance: Double,
    val userId: Long,
    val accountType: String
){
    fun toAccount(): Account {
        return Account(id, accountNumber, balance, userId, accountType)
    }
}

data class AccountResponseDto(
    val content: List<AccountDto>,
    val pageable: PageableDto,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: SortDto,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
){
    fun toAccountResponse(): AccountResponse{
        return AccountResponse(
            content = content.map { it ->
                it.toAccount()
            },
            pageable.toPageable(),
            totalPages,
            totalElements,
            last,
            size,
            number,
            sort.toSort(),
            numberOfElements,
            first,
            empty
        )
    }
}
data class PageableDto(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortDto,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
){
    fun toPageable(): Pageable{
        return Pageable(
            pageNumber, pageSize, sort.toSort(), offset, paged, unpaged
        )
    }
}

data class SortDto(
    val sorted: Boolean,
    val empty: Boolean,
    val unsorted: Boolean
){
    fun toSort(): Sort{
        return Sort(
            sorted, empty, unsorted
        )
    }
}
