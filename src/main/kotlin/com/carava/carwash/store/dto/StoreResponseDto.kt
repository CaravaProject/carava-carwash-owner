package com.carava.carwash.store.dto

import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.entity.StoreCategory
import com.carava.carwash.store.entity.StoreStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 매장 정보 응답 DTO
 */
@Schema(description = "매장 정보 응답")
data class StoreResponseDto(
    
    @Schema(description = "매장 ID", example = "1")
    val id: Long,
    
    @Schema(description = "사장님 ID", example = "1")
    val ownerMemberId: Long,
    
    @Schema(description = "매장명", example = "카라바 세차장")
    val name: String,
    
    @Schema(description = "매장 설명", example = "깨끗하고 정성스러운 세차 서비스를 제공합니다")
    val description: String?,
    
    @Schema(description = "매장 전화번호", example = "02-1234-5678")
    val phone: String?,
    
    @Schema(description = "주소 ID", example = "1")
    val addressId: Long,
    
    @Schema(description = "매장 카테고리", example = "CAR_WASH")
    val category: StoreCategory,
    
    @Schema(description = "매장 상태", example = "ACTIVE")
    val status: StoreStatus,
    
    @Schema(description = "평균 평점", example = "4.5")
    val averageRating: BigDecimal,
    
    @Schema(description = "총 리뷰 수", example = "25")
    val totalReviews: Int,
    
    @Schema(description = "조회 수", example = "150")
    val viewCount: Int,
    
    @Schema(description = "즐겨찾기 수", example = "12")
    val favoriteCount: Int,
    
    @Schema(description = "등록일시", example = "2024-01-01T00:00:00")
    val createdAt: LocalDateTime,
    
    @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(store: Store): StoreResponseDto {
            return StoreResponseDto(
                id = store.id,
                ownerMemberId = store.ownerMemberId,
                name = store.name,
                description = store.description,
                phone = store.phone,
                addressId = store.addressId,
                category = store.category,
                status = store.status,
                averageRating = store.averageRating,
                totalReviews = store.totalReviews,
                viewCount = store.viewCount,
                favoriteCount = store.favoriteCount,
                createdAt = store.createdAt,
                updatedAt = store.updatedAt
            )
        }
    }
}

/**
 * 매장 목록 응답 DTO (간단한 정보)
 */
@Schema(description = "매장 목록 응답")
data class StoreListResponseDto(
    
    @Schema(description = "매장 ID", example = "1")
    val id: Long,
    
    @Schema(description = "매장명", example = "카라바 세차장")
    val name: String,
    
    @Schema(description = "매장 카테고리", example = "CAR_WASH")
    val category: StoreCategory,
    
    @Schema(description = "매장 상태", example = "ACTIVE")
    val status: StoreStatus,
    
    @Schema(description = "평균 평점", example = "4.5")
    val averageRating: BigDecimal,
    
    @Schema(description = "총 리뷰 수", example = "25")
    val totalReviews: Int,
    
    @Schema(description = "등록일시", example = "2024-01-01T00:00:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(store: Store): StoreListResponseDto {
            return StoreListResponseDto(
                id = store.id,
                name = store.name,
                category = store.category,
                status = store.status,
                averageRating = store.averageRating,
                totalReviews = store.totalReviews,
                createdAt = store.createdAt
            )
        }
    }
} 