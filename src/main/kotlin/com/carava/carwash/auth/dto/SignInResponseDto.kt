package com.carava.carwash.auth.dto

data class SignInResponseDto (
    val token: String,
    val authId: Long,
    val email: String,
    val expiresIn: Long? = null // 토큰 만료 시간(초)
)