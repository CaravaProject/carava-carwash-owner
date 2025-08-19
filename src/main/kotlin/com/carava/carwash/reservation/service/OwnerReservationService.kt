package com.carava.carwash.reservation.service

import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.global.exception.*
import com.carava.carwash.reservation.dto.*
import com.carava.carwash.reservation.entity.Reservation
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.repository.ReservationRepository
import com.carava.carwash.store.repository.StoreRepository
import com.carava.carwash.store.service.StoreService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class OwnerReservationService(
    private val reservationRepository: ReservationRepository,
    private val storeRepository: StoreRepository,
    private val storeService: StoreService
) {

    /**
     * 매장의 예약 목록 조회 (페이징)
     */
    fun getStoreReservations(
        ownerId: Long,
        storeId: Long,
        status: ReservationStatus?,
        date: LocalDate?,
        page: Int,
        size: Int
    ): ApiResponse<Page<OwnerReservationListResponseDto>> {
        // 매장 소유권 검증 (throw 방식)
        storeService.validateStoreOwnership(storeId, ownerId)
        
        return try {
            val pageable: Pageable = PageRequest.of(page, size, Sort.by("reservationDate", "reservationTime").descending())
            
            val reservationsPage = when {
                status != null -> reservationRepository.findByStoreIdAndStatus(storeId, status, pageable)
                else -> reservationRepository.findByStoreId(storeId, pageable)
            }

            val responsePage = reservationsPage.map { reservation ->
                OwnerReservationListResponseDto(
                    id = reservation.id,
                    customerName = "고객${reservation.customerMemberId}", // 실제로는 고객 정보 조회 필요
                    customerPhone = "010-****-****", // 실제로는 고객 정보 조회 필요
                    carInfo = "차량 정보", // 실제로는 차량 정보 조회 필요
                    reservationDate = reservation.reservationDate,
                    reservationTime = reservation.reservationTime,
                    status = reservation.status,
                    finalAmount = reservation.finalAmount,
                    serviceCount = 1, // 실제로는 서비스 개수 조회 필요
                    isUrgent = isUrgentReservation(reservation)
                )
            }

            ApiResponse.success(responsePage, "예약 목록 조회 완료")
        } catch (e: Exception) {
            throw RuntimeException("예약 목록 조회 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    /**
     * 예약 상세 조회
     */
    fun getReservationDetail(
        ownerId: Long,
        storeId: Long,
        reservationId: Long
    ): ApiResponse<OwnerReservationResponseDto> {
        return try {
            // 매장 소유권 검증
            storeService.validateStoreOwnership(storeId, ownerId)

            val reservation = reservationRepository.findById(reservationId)
                .orElseThrow { ReservationNotFoundException("예약을 찾을 수 없습니다") }

            if (reservation.storeId != storeId) {
                throw ReservationNotFoundException("해당 매장의 예약이 아닙니다")
            }

            val response = OwnerReservationResponseDto.from(
                reservation = reservation,
                customerName = "고객${reservation.customerMemberId}", // 실제로는 고객 정보 조회 필요
                customerPhone = "010-****-****", // 실제로는 고객 정보 조회 필요
                carInfo = "차량 정보", // 실제로는 차량 정보 조회 필요
                services = listOf(
                    ServiceDto("기본 세차", 1, BigDecimal("15000"))
                ) // 실제로는 예약 메뉴 조회 필요
            )

            ApiResponse.success(response, "예약 상세 조회 완료")
        } catch (e: Exception) {
            throw RuntimeException("예약 상세 조회 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    /**
     * 예약 승인
     */
    @Transactional
    fun approveReservation(
        ownerId: Long,
        storeId: Long,
        reservationId: Long
    ): ApiResponse<Nothing> {
        return try {
            // 매장 소유권 검증
            storeService.validateStoreOwnership(storeId, ownerId)

            val reservation = reservationRepository.findById(reservationId)
                .orElseThrow { ReservationNotFoundException("예약을 찾을 수 없습니다") }

            if (reservation.storeId != storeId) {
                throw ReservationNotFoundException("해당 매장의 예약이 아닙니다")
            }

            if (reservation.status != ReservationStatus.PENDING) {
                throw InvalidReservationStatusException("대기 중인 예약만 승인할 수 있습니다")
            }

            reservation.confirm()
            reservationRepository.save(reservation)

            ApiResponse.success(message = "예약이 승인되었습니다")
        } catch (e: Exception) {
            throw RuntimeException("예약 승인 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    /**
     * 예약 거절
     */
    @Transactional
    fun rejectReservation(
        ownerId: Long,
        storeId: Long,
        reservationId: Long,
        reason: String
    ): ApiResponse<Nothing> {
        return try {
            // 매장 소유권 검증
            storeService.validateStoreOwnership(storeId, ownerId)

            val reservation = reservationRepository.findById(reservationId)
                .orElseThrow { ReservationNotFoundException("예약을 찾을 수 없습니다") }

            if (reservation.storeId != storeId) {
                throw ReservationNotFoundException("해당 매장의 예약이 아닙니다")
            }

            if (reservation.status != ReservationStatus.PENDING) {
                throw InvalidReservationStatusException("대기 중인 예약만 거절할 수 있습니다")
            }

            reservation.reject(reason)
            reservationRepository.save(reservation)

            ApiResponse.success(message = "예약이 거절되었습니다")
        } catch (e: Exception) {
            throw RuntimeException("예약 거절 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    /**
     * 예약 상태 변경 (서비스 시작, 완료, 노쇼 등)
     */
    @Transactional
    fun updateReservationStatus(
        ownerId: Long,
        storeId: Long,
        reservationId: Long,
        newStatus: ReservationStatus
    ): ApiResponse<Nothing> {
        return try {
            // 매장 소유권 검증
            storeService.validateStoreOwnership(storeId, ownerId)

            val reservation = reservationRepository.findById(reservationId)
                .orElseThrow { ReservationNotFoundException("예약을 찾을 수 없습니다") }

            if (reservation.storeId != storeId) {
                throw ReservationNotFoundException("해당 매장의 예약이 아닙니다")
            }

            when (newStatus) {
                ReservationStatus.IN_PROGRESS -> reservation.startService()
                ReservationStatus.COMPLETED -> reservation.complete()
                ReservationStatus.NO_SHOW -> reservation.markAsNoShow()
                else -> throw InvalidReservationStatusException("지원하지 않는 상태 변경입니다")
            }

            reservationRepository.save(reservation)

            val message = when (newStatus) {
                ReservationStatus.IN_PROGRESS -> "서비스가 시작되었습니다"
                ReservationStatus.COMPLETED -> "서비스가 완료되었습니다"
                ReservationStatus.NO_SHOW -> "노쇼 처리되었습니다"
                else -> "상태가 변경되었습니다"
            }

            ApiResponse.success(message = message)
        } catch (e: Exception) {
            throw RuntimeException("상태 변경 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    /**
     * 매장 예약 현황 요약
     */
    fun getReservationSummary(
        ownerId: Long,
        storeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): ApiResponse<ReservationSummaryDto> {
        return try {
            // 매장 소유권 검증
            storeService.validateStoreOwnership(storeId, ownerId)

            val pendingCount = reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.PENDING)
            val confirmedCount = reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.CONFIRMED)
            val inProgressCount = reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.IN_PROGRESS)
            val completedCount = reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.COMPLETED)
            val cancelledCount = reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.CANCELLED)
            val noShowCount = reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.NO_SHOW)

            // 오늘 예약들
            val todayReservations = reservationRepository.findByStoreIdAndReservationDate(storeId, LocalDate.now())
            val todayRevenue = todayReservations
                .filter { it.status == ReservationStatus.COMPLETED }
                .sumOf { it.finalAmount }

            val summary = ReservationSummaryDto(
                pendingCount = pendingCount,
                confirmedCount = confirmedCount,
                inProgressCount = inProgressCount,
                completedCount = completedCount,
                cancelledCount = cancelledCount,
                noShowCount = noShowCount,
                todayRevenue = todayRevenue,
                totalReservations = pendingCount + confirmedCount + inProgressCount + completedCount + cancelledCount + noShowCount
            )

            ApiResponse.success(summary, "예약 현황 요약 조회 완료")
        } catch (e: Exception) {
            throw RuntimeException("예약 현황 조회 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    /**
     * 긴급 예약 여부 판단 (예약 시간이 2시간 이내인 경우)
     */
    private fun isUrgentReservation(reservation: Reservation): Boolean {
        val reservationDateTime = LocalDateTime.of(reservation.reservationDate, reservation.reservationTime)
        val now = LocalDateTime.now()
        return reservationDateTime.isAfter(now) && reservationDateTime.isBefore(now.plusHours(2))
    }
} 