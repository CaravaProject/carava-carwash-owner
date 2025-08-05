package com.carava.carwash.auth.dto

import com.carava.carwash.auth.entity.Auth
import com.carava.carwash.owner.entity.OwnerMember
import java.time.format.DateTimeFormatter

data class SignUpResponseDto (
    val authId: Long,
    val email: String,
    val name: String,
    val businessName: String,
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