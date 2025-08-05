package com.carava.carwash.reservation.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/**
 * 매장 예약 현황 요약 DTO
 */
@Schema(description = "매장 예약 현황 요약")
data class ReservationSummaryDto(
    @Schema(description = "대기 중인 예약 수", example = "5")
    val pendingCount: Long,
    
    @Schema(description = "승인된 예약 수", example = "10")
    val confirmedCount: Long,
    
    @Schema(description = "진행 중인 예약 수", example = "2")
    val inProgressCount: Long,
    
    @Schema(description = "완료된 예약 수", example = "25")
    val completedCount: Long,
    
    @Schema(description = "취소된 예약 수", example = "3")
    val cancelledCount: Long,
    
    @Schema(description = "노쇼 예약 수", example = "1")
    val noShowCount: Long,
    
    @Schema(description = "오늘 매출", example = "150000")
    val todayRevenue: BigDecimal,
    
    @Schema(description = "총 예약 수", example = "46")
    val totalReservations: Long
)

/**
 * 예약 거절 요청 DTO
 */
@Schema(description = "예약 거절 요청")
data class RejectReservationRequestDto(
    @Schema(description = "거절 사유", example = "매장 사정으로 인한 예약 거절", required = true)
    val reason: String
)

/**
 * 예약 상태 변경 요청 DTO
 */
@Schema(description = "예약 상태 변경 요청")
data class UpdateReservationStatusRequestDto(
    @Schema(description = "변경할 상태", example = "IN_PROGRESS", allowableValues = ["IN_PROGRESS", "COMPLETED", "NO_SHOW"], required = true)
    val status: String
) 