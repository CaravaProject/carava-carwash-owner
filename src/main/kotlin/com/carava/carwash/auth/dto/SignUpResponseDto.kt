package com.carava.carwash.auth.dto

data class SignUpResponseDto (
    val authId: Long,
    val email: String,
    val createdAt: String? = null
)