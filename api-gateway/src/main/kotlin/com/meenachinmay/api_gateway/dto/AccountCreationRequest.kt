package com.meenachinmay.api_gateway.dto

data class AccountDetails(
    val accountId: String,
    val name: String,
    val phoneNumber: String,
    val prefecture: String
)

data class AccountsResponse(
    val accounts: List<AccountDetails>
)