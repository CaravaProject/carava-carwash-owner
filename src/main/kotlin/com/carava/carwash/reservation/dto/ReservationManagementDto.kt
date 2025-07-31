package com.carava.carwash.reservation.dto

import com.carava.carwash.reservation.entity.ReservationStatus
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class ReservationApprovalRequestDto(
    @field:NotBlank(message = "승인/거절 사유는 필수입니다")
    val reason: String? = null
)

data class ReservationStatusUpdateDto(
    val newStatus: ReservationStatus,
    val note: String? = null
)

data class DailyScheduleResponseDto(
    val date: LocalDate,
    val totalReservations: Int,
    val pendingCount: Int,
    val confirmedCount: Int,
    val inProgressCount: Int,
    val completedCount: Int,
    val reservations: List<OwnerReservationListResponseDto>
)

data class ReservationSummaryDto(
    val totalReservations: Long,
    val pendingReservations: Long,
    val confirmedReservations: Long,
    val completedReservations: Long,
    val totalRevenue: String // BigDecimal을 String으로 변환하여 전송
) 