package com.meenachinmay.api_gateway.dto

data class AccountCreationRequest(
    val name: String,
    val phoneNumber: String,
    val prefecture: String
)

data class AccountFetchRequest (
    val phoneNumber: String
)