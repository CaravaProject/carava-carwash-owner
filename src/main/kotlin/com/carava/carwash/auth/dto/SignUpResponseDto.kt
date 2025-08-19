package com.carava.carwash.auth.dto

import com.carava.carwash.auth.entity.Auth
import com.carava.carwash.owner.entity.OwnerMember
import io.swagger.v3.oas.annotations.media.Schema
import java.time.format.DateTimeFormatter

@Schema(description = "사업자 회원가입 응답")
data class SignUpResponseDto(
    @Schema(description = "인증 ID", example = "1")
    val authId: Long,
    
    @Schema(description = "이메일", example = "owner@example.com")
    val email: String,
    
    @Schema(description = "사업자 이름", example = "김사장")
    val name: String,
    
    @Schema(description = "사업체명", example = "깨끗한 세차장")
    val businessName: String,
    
    @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
    val createdAt: String? = null
) {
    companion object {
        fun from(auth: Auth, owner: OwnerMember): SignUpResponseDto {
            return SignUpResponseDto(
                authId = auth.id,
                email = auth.getEmail(),
                name = owner.name,
                businessName = owner.businessName,
                createdAt = auth.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }
    }
}