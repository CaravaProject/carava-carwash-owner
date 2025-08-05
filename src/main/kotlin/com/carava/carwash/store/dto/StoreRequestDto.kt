package com.carava.carwash.store.dto

import com.carava.carwash.store.entity.StoreCategory
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 매장 등록 요청 DTO
 */
@Schema(description = "매장 등록 요청")
data class CreateStoreRequestDto(
    
    @field:NotBlank(message = "매장명은 필수입니다")
    @field:Size(min = 2, max = 100, message = "매장명은 2자 이상 100자 이하여야 합니다")
    @Schema(description = "매장명", example = "카라바 세차장")
    val name: String,
    
    @field:Size(max = 1000, message = "매장 설명은 1000자 이하여야 합니다")
    @Schema(description = "매장 설명", example = "깨끗하고 정성스러운 세차 서비스를 제공합니다")
    val description: String?,
    
    @field:Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 포함할 수 있습니다")
    @field:Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    @Schema(description = "매장 전화번호", example = "02-1234-5678")
    val phone: String?,
    
    @field:NotNull(message = "주소 ID는 필수입니다")
    @Schema(description = "주소 ID", example = "1")
    val addressId: Long,
    
    @field:NotNull(message = "매장 카테고리는 필수입니다")
    @Schema(description = "매장 카테고리", example = "CAR_WASH")
    val category: StoreCategory
)

/**
 * 매장 수정 요청 DTO
 */
@Schema(description = "매장 정보 수정 요청")
data class UpdateStoreRequestDto(
    
    @field:Size(min = 2, max = 100, message = "매장명은 2자 이상 100자 이하여야 합니다")
    @Schema(description = "매장명", example = "카라바 세차장")
    val name: String?,
    
    @field:Size(max = 1000, message = "매장 설명은 1000자 이하여야 합니다")
    @Schema(description = "매장 설명", example = "깨끗하고 정성스러운 세차 서비스를 제공합니다")
    val description: String?,
    
    @field:Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 포함할 수 있습니다")
    @field:Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    @Schema(description = "매장 전화번호", example = "02-1234-5678")
    val phone: String?
) 