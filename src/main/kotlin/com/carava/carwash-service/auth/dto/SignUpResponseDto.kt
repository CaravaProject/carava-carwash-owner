package com.carava.`carwash-service`.auth.dto

data class SignUpResponseDto (
    val authId: Long,
    val email: String,
    val createdAt: String? = null
)