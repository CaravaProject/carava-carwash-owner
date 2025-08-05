package com.carava.carwash.auth.dto

data class SignUpRequestDto (
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
    val businessNumber: String,
    val businessName: String
)