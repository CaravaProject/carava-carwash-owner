package com.carava.carwash.menu.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.math.BigDecimal

/**
 * 메뉴 등록 요청 DTO
 */
@Schema(description = "메뉴 등록 요청")
data class CreateMenuRequestDto(
    
    @field:NotBlank(message = "카테고리명은 필수입니다")
    @field:Size(min = 1, max = 50, message = "카테고리명은 1자 이상 50자 이하여야 합니다")
    @Schema(description = "메뉴 카테고리명", example = "기본세차")
    val categoryName: String,
    
    @field:NotBlank(message = "메뉴명은 필수입니다")
    @field:Size(min = 1, max = 100, message = "메뉴명은 1자 이상 100자 이하여야 합니다")
    @Schema(description = "메뉴명", example = "실내외 풀세차")
    val name: String,
    
    @field:Size(max = 1000, message = "메뉴 설명은 1000자 이하여야 합니다")
    @Schema(description = "메뉴 설명", example = "실내외 완벽 세차 서비스입니다")
    val description: String?,
    
    @field:NotNull(message = "가격은 필수입니다")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @field:Digits(integer = 8, fraction = 2, message = "가격은 최대 8자리 정수, 2자리 소수까지 가능합니다")
    @Schema(description = "메뉴 가격", example = "25000")
    val price: BigDecimal,
    
    @field:NotNull(message = "소요시간은 필수입니다")
    @field:Min(value = 1, message = "소요시간은 1분 이상이어야 합니다")
    @field:Max(value = 1440, message = "소요시간은 1440분(24시간) 이하여야 합니다")
    @Schema(description = "소요시간(분)", example = "60")
    val duration: Int,
    
    @Schema(description = "지원 차량 타입 (JSON 형태)", example = "\"[\\\"SEDAN\\\", \\\"SUV\\\"]\"")
    val carTypes: String?,
    
    @field:Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "표시 순서", example = "1")
    val displayOrder: Int = 0
)

/**
 * 메뉴 수정 요청 DTO
 */
@Schema(description = "메뉴 수정 요청")
data class UpdateMenuRequestDto(
    
    @field:Size(min = 1, max = 50, message = "카테고리명은 1자 이상 50자 이하여야 합니다")
    @Schema(description = "메뉴 카테고리명", example = "기본세차")
    val categoryName: String?,
    
    @field:Size(min = 1, max = 100, message = "메뉴명은 1자 이상 100자 이하여야 합니다")
    @Schema(description = "메뉴명", example = "실내외 풀세차")
    val name: String?,
    
    @field:Size(max = 1000, message = "메뉴 설명은 1000자 이하여야 합니다")
    @Schema(description = "메뉴 설명", example = "실내외 완벽 세차 서비스입니다")
    val description: String?,
    
    @field:DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @field:Digits(integer = 8, fraction = 2, message = "가격은 최대 8자리 정수, 2자리 소수까지 가능합니다")
    @Schema(description = "메뉴 가격", example = "25000")
    val price: BigDecimal?,
    
    @field:Min(value = 1, message = "소요시간은 1분 이상이어야 합니다")
    @field:Max(value = 1440, message = "소요시간은 1440분(24시간) 이하여야 합니다")
    @Schema(description = "소요시간(분)", example = "60")
    val duration: Int?,
    
    @Schema(description = "지원 차량 타입 (JSON 형태)", example = "\"[\\\"SEDAN\\\", \\\"SUV\\\"]\"")
    val carTypes: String?
)

/**
 * 메뉴 순서 변경 요청 DTO
 */
@Schema(description = "메뉴 순서 변경 요청")
data class UpdateMenuOrderRequestDto(
    
    @field:NotEmpty(message = "메뉴 순서 목록은 필수입니다")
    @Schema(description = "메뉴 ID와 순서 목록")
    val menuOrders: List<MenuOrderDto>
)

@Schema(description = "메뉴 순서 정보")
data class MenuOrderDto(
    
    @field:NotNull(message = "메뉴 ID는 필수입니다")
    @Schema(description = "메뉴 ID", example = "1")
    val menuId: Long,
    
    @field:Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "표시 순서", example = "1")
    val displayOrder: Int
) 