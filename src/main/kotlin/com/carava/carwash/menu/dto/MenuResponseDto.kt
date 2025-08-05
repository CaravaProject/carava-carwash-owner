package com.carava.carwash.menu.dto

import com.carava.carwash.menu.entity.Menu
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 메뉴 정보 응답 DTO
 */
@Schema(description = "메뉴 정보 응답")
data class MenuResponseDto(
    
    @Schema(description = "메뉴 ID", example = "1")
    val id: Long,
    
    @Schema(description = "매장 ID", example = "1")
    val storeId: Long,
    
    @Schema(description = "카테고리명", example = "기본세차")
    val categoryName: String,
    
    @Schema(description = "메뉴명", example = "실내외 풀세차")
    val name: String,
    
    @Schema(description = "메뉴 설명", example = "실내외 완벽 세차 서비스입니다")
    val description: String?,
    
    @Schema(description = "가격", example = "25000")
    val price: BigDecimal,
    
    @Schema(description = "소요시간(분)", example = "60")
    val duration: Int,
    
    @Schema(description = "지원 차량 타입", example = "[\"SEDAN\", \"SUV\"]")
    val carTypes: String?,
    
    @Schema(description = "활성 상태", example = "true")
    val isActive: Boolean,
    
    @Schema(description = "표시 순서", example = "1")
    val displayOrder: Int,
    
    @Schema(description = "등록일시", example = "2024-01-01T00:00:00")
    val createdAt: LocalDateTime,
    
    @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(menu: Menu): MenuResponseDto {
            return MenuResponseDto(
                id = menu.id,
                storeId = menu.storeId,
                categoryName = menu.categoryName,
                name = menu.name,
                description = menu.description,
                price = menu.price,
                duration = menu.duration,
                carTypes = menu.carTypes,
                isActive = menu.isActive,
                displayOrder = menu.displayOrder,
                createdAt = menu.createdAt,
                updatedAt = menu.updatedAt
            )
        }
    }
}

/**
 * 메뉴 목록 응답 DTO (간단한 정보)
 */
@Schema(description = "메뉴 목록 응답")
data class MenuListResponseDto(
    
    @Schema(description = "메뉴 ID", example = "1")
    val id: Long,
    
    @Schema(description = "카테고리명", example = "기본세차")
    val categoryName: String,
    
    @Schema(description = "메뉴명", example = "실내외 풀세차")
    val name: String,
    
    @Schema(description = "가격", example = "25000")
    val price: BigDecimal,
    
    @Schema(description = "소요시간(분)", example = "60")
    val duration: Int,
    
    @Schema(description = "활성 상태", example = "true")
    val isActive: Boolean,
    
    @Schema(description = "표시 순서", example = "1")
    val displayOrder: Int
) {
    companion object {
        fun from(menu: Menu): MenuListResponseDto {
            return MenuListResponseDto(
                id = menu.id,
                categoryName = menu.categoryName,
                name = menu.name,
                price = menu.price,
                duration = menu.duration,
                isActive = menu.isActive,
                displayOrder = menu.displayOrder
            )
        }
    }
}

/**
 * 카테고리별 메뉴 목록 응답 DTO
 */
@Schema(description = "카테고리별 메뉴 목록 응답")
data class CategoryMenuResponseDto(
    
    @Schema(description = "카테고리명", example = "기본세차")
    val categoryName: String,
    
    @Schema(description = "메뉴 목록")
    val menus: List<MenuListResponseDto>
) {
    companion object {
        fun from(categoryName: String, menus: List<Menu>): CategoryMenuResponseDto {
            return CategoryMenuResponseDto(
                categoryName = categoryName,
                menus = menus.map { MenuListResponseDto.from(it) }
            )
        }
    }
}

/**
 * 매장 메뉴 요약 응답 DTO
 */
@Schema(description = "매장 메뉴 요약 응답")
data class StoreMenuSummaryResponseDto(
    
    @Schema(description = "총 메뉴 수", example = "10")
    val totalMenuCount: Long,
    
    @Schema(description = "활성 메뉴 수", example = "8")
    val activeMenuCount: Long,
    
    @Schema(description = "카테고리 목록")
    val categories: List<String>,
    
    @Schema(description = "최근 등록 메뉴")
    val recentMenus: List<MenuListResponseDto>
) 