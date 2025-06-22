package com.carava.carwash.customer.dto

data class SignInResponseDto (
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)