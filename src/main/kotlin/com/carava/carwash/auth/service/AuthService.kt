package com.carava.carwash.auth.service

import com.carava.carwash.auth.dto.SignInRequestDto
import com.carava.carwash.auth.dto.SignInResponseDto
import com.carava.carwash.auth.dto.SignUpRequestDto
import com.carava.carwash.auth.dto.SignUpResponseDto
import com.carava.carwash.global.dto.ApiResponse
import org.springframework.stereotype.Service

@Service
class AuthService {
    
    fun signUp(request: SignUpRequestDto): ApiResponse<SignUpResponseDto> {
        // 임시 응답
        val responseDto = SignUpResponseDto(
            authId = 1L,
            email = request.email,
            name = request.name,
            businessName = request.businessName,
            createdAt = "2024-01-01T00:00:00"
        )
        return ApiResponse.success(data = responseDto, message = "회원가입이 완료되었습니다")
    }

    fun signIn(request: SignInRequestDto): ApiResponse<SignInResponseDto> {
        // 임시 응답
        val responseDto = SignInResponseDto(
            token = "temp-token",
            authId = 1L,
            email = request.email,
            expiresIn = 86400
        )
        return ApiResponse.success(data = responseDto, message = "로그인이 완료되었습니다")
    }
}