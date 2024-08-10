package com.meenachinmay.api_gateway.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthenticationRequest(val email: String, val password: String)
data class AuthenticationResponse(val token: String)