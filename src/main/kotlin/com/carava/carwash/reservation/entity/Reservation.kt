package com.carava.carwash.reservation.entity

import com.carava.carwash.global.entity.BaseEntity
import com.carava.carwash.global.exception.InvalidReservationStatusException
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(
    name = "reservation",
    indexes = [
        Index(name = "idx_reservation_customer_member_id", columnList = "customer_member_id"),
        Index(name = "idx_reservation_store_id", columnList = "store_id"),
        Index(name = "idx_reservation_car_id", columnList = "car_id"),
        Index(name = "idx_reservation_date", columnList = "reservation_date"),
        Index(name = "idx_reservation_status", columnList = "status"),
        Index(name = "idx_reservation_store_date_time", columnList = "store_id, reservation_date, reservation_time", unique = true)
    ]
)
class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "customer_member_id", nullable = false)
    val customerMemberId: Long,

    @Column(name = "store_id", nullable = false)
    val storeId: Long,

    @Column(name = "car_id", nullable = false)
    val carId: Long,

    @Column(name = "reservation_date", nullable = false)
    val reservationDate: LocalDate,

    @Column(name = "reservation_time", nullable = false)
    val reservationTime: LocalTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus = ReservationStatus.PENDING,

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    val discountAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    val finalAmount: BigDecimal,

    @Column(name = "customer_request", columnDefinition = "TEXT")
    val customerRequest: String? = null,

    @Column(name = "image_urls", columnDefinition = "JSONB")
    val imageUrls: String? = null,

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    var rejectionReason: String? = null,

    @Column(name = "estimated_duration")
    val estimatedDuration: Int? = null
) : BaseEntity() {

    fun confirm(): Reservation {
        if (status != ReservationStatus.PENDING) {
            throw InvalidReservationStatusException("대기 중인 예약만 승인할 수 있습니다")
        }
        status = ReservationStatus.CONFIRMED
        return this
    }

    fun reject(reason: String): Reservation {
        if (status != ReservationStatus.PENDING) {
            throw InvalidReservationStatusException("대기 중인 예약만 거절할 수 있습니다")
        }
        status = ReservationStatus.CANCELLED
        rejectionReason = reason
        return this
    }

    fun startService(): Reservation {
        if (status != ReservationStatus.CONFIRMED) {
            throw InvalidReservationStatusException("승인된 예약만 서비스를 시작할 수 있습니다")
        }
        status = ReservationStatus.IN_PROGRESS
        return this
    }

    fun complete(): Reservation {
        if (status != ReservationStatus.IN_PROGRESS) {
            throw InvalidReservationStatusException("진행 중인 예약만 완료할 수 있습니다")
        }
        status = ReservationStatus.COMPLETED
        return this
    }

    fun markAsNoShow(): Reservation {
        if (status != ReservationStatus.CONFIRMED) {
            throw InvalidReservationStatusException("승인된 예약만 노쇼 처리할 수 있습니다")
        }
        status = ReservationStatus.NO_SHOW
        return this
    }

    fun isManageable(): Boolean {
        return status in listOf(ReservationStatus.PENDING, ReservationStatus.CONFIRMED, ReservationStatus.IN_PROGRESS)
    }
} 