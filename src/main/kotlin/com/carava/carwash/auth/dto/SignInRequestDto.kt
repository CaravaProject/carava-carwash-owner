package com.carava.carwash.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

@Schema(description = "사업자 로그인 요청")
data class SignInRequestDto(
    @Schema(description = "이메일 주소", example = "owner@example.com")
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @Schema(description = "비밀번호", example = "Password123!")
    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String
)