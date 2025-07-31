package com.carava.carwash.reservation.dto

import com.carava.carwash.reservation.entity.Reservation
import com.carava.carwash.reservation.entity.ReservationStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class OwnerReservationResponseDto(
    val id: Long,
    val customerName: String,
    val customerPhone: String,
    val carInfo: String, // "현대 아반떼 (12가3456)"
    val reservationDate: LocalDate,
    val reservationTime: LocalTime,
    val status: ReservationStatus,
    val totalAmount: BigDecimal,
    val finalAmount: BigDecimal,
    val customerRequest: String?,
    val rejectionReason: String?,
    val estimatedDuration: Int?,
    val services: List<ServiceDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(
            reservation: Reservation,
            customerName: String,
            customerPhone: String,
            carInfo: String,
            services: List<ServiceDto>
        ): OwnerReservationResponseDto {
            return OwnerReservationResponseDto(
                id = reservation.id,
                customerName = customerName,
                customerPhone = customerPhone,
                carInfo = carInfo,
                reservationDate = reservation.reservationDate,
                reservationTime = reservation.reservationTime,
                status = reservation.status,
                totalAmount = reservation.totalAmount,
                finalAmount = reservation.finalAmount,
                customerRequest = reservation.customerRequest,
                rejectionReason = reservation.rejectionReason,
                estimatedDuration = reservation.estimatedDuration,
                services = services,
                createdAt = reservation.createdAt,
                updatedAt = reservation.updatedAt
            )
        }
    }
}

data class ServiceDto(
    val serviceName: String,
    val quantity: Int,
    val price: BigDecimal
)

data class OwnerReservationListResponseDto(
    val id: Long,
    val customerName: String,
    val customerPhone: String,
    val carInfo: String,
    val reservationDate: LocalDate,
    val reservationTime: LocalTime,
    val status: ReservationStatus,
    val finalAmount: BigDecimal,
    val serviceCount: Int,
    val isUrgent: Boolean = false // 예약 시간이 임박한 경우
) 