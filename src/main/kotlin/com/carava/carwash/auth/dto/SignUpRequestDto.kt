package com.carava.carwash.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

@Schema(description = "사업자 회원가입 요청")
data class SignUpRequestDto(
    @Schema(description = "이메일 주소", example = "owner@example.com")
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @Schema(description = "비밀번호 (8자 이상)", example = "Password123!")
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다")
    val password: String,

    @Schema(description = "사업자 이름", example = "김사장")
    @field:NotBlank(message = "이름은 필수입니다")
    @field:Size(min = 2, max = 20, message = "이름은 2~20자리여야 합니다")
    val name: String,

    @Schema(description = "전화번호", example = "010-1234-5678")
    @field:NotBlank(message = "전화번호는 필수입니다")
    @field:Pattern(
        regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$",
        message = "올바른 전화번호 형식이 아닙니다 (예: 010-1234-5678)"
    )
    val phone: String,

    @Schema(description = "사업자 등록번호", example = "123-45-67890")
    @field:NotBlank(message = "사업자 등록번호는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$",
        message = "올바른 사업자 등록번호 형식이 아닙니다 (예: 123-45-67890)"
    )
    val businessNumber: String,

    @Schema(description = "사업체명", example = "깨끗한 세차장")
    @field:NotBlank(message = "사업체명은 필수입니다")
    @field:Size(min = 2, max = 50, message = "사업체명은 2~50자리여야 합니다")
    val businessName: String
)