package com.example.simplebankingapp.domain.entity

data class Account(
    val id: Int,
    val accountNumber: String,
    val balance: Double,
    val userId: Long,
    val accountType: String
)
data class AccountResponse(
    val content: List<Account>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)


data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: Sort,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class Sort(
    val sorted: Boolean,
    val empty: Boolean,
    val unsorted: Boolean
)