package com.carava.carwash.auth.dto

import com.carava.carwash.auth.entity.Auth

data class SignInResponseDto (
    val token: String,
    val authId: Long,
    val email: String,
    val expiresIn: Long? = null // 토큰 만료 시간(초)
) {
    companion object {
        fun from(auth: Auth, token: String): SignInResponseDto {
            return SignInResponseDto(
                token = token,
                authId = auth.id,
                email = auth.getEmail(),
                expiresIn = 86400 // 24시간
            )
        }
    }
}