package com.carava.carwash.owner.dto

data class SignInResponseDto (
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)