package com.carava.carwash.auth.dto

import com.carava.carwash.auth.entity.Auth
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사업자 로그인 응답")
data class SignInResponseDto(
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val token: String,
    
    @Schema(description = "인증 ID", example = "1")
    val authId: Long,
    
    @Schema(description = "이메일", example = "owner@example.com")
    val email: String,
    
    @Schema(description = "토큰 만료 시간(초)", example = "1800")
    val expiresIn: Long? = null
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