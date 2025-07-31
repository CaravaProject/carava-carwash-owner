package com.carava.carwash.reservation.service

import com.carava.carwash.reservation.dto.*
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.repository.ReservationRepository
import com.carava.carwash.shared.dto.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional
class OwnerReservationService(
    private val reservationRepository: ReservationRepository
) {

    @Transactional(readOnly = true)
    fun getStoreReservations(
        storeId: Long,
        pageable: Pageable
    ): ApiResponse<Page<OwnerReservationListResponseDto>> {
        val reservations = reservationRepository.findByStoreId(storeId, pageable)
        
        val responseDto = reservations.map { reservation ->
            // TODO: 실제로는 고객 정보와 차량 정보를 조회해야 함
            OwnerReservationListResponseDto(
                id = reservation.id,
                customerName = "고객${reservation.customerMemberId}", // TODO: 실제 고객명 조회
                customerPhone = "010-0000-0000", // TODO: 실제 전화번호 조회
                carInfo = "차량 정보", // TODO: 실제 차량 정보 조회
                reservationDate = reservation.reservationDate,
                reservationTime = reservation.reservationTime,
                status = reservation.status,
                finalAmount = reservation.finalAmount,
                serviceCount = 1, // TODO: 실제 서비스 개수 조회
                isUrgent = isUrgentReservation(reservation.reservationDate, reservation.reservationTime)
            )
        }

        return ApiResponse.success(responseDto, "매장 예약 목록 조회가 완료되었습니다")
    }

    @Transactional(readOnly = true)
    fun getStoreReservationsByStatus(
        storeId: Long,
        status: ReservationStatus,
        pageable: Pageable
    ): ApiResponse<Page<OwnerReservationListResponseDto>> {
        val reservations = reservationRepository.findByStoreIdAndStatus(storeId, status, pageable)
        
        val responseDto = reservations.map { reservation ->
            OwnerReservationListResponseDto(
                id = reservation.id,
                customerName = "고객${reservation.customerMemberId}",
                customerPhone = "010-0000-0000",
                carInfo = "차량 정보",
                reservationDate = reservation.reservationDate,
                reservationTime = reservation.reservationTime,
                status = reservation.status,
                finalAmount = reservation.finalAmount,
                serviceCount = 1,
                isUrgent = isUrgentReservation(reservation.reservationDate, reservation.reservationTime)
            )
        }

        return ApiResponse.success(responseDto, "매장 예약 목록 조회가 완료되었습니다")
    }

    @Transactional(readOnly = true)
    fun getReservationDetail(
        storeId: Long,
        reservationId: Long
    ): ApiResponse<OwnerReservationResponseDto> {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 예약입니다") }

        if (reservation.storeId != storeId) {
            throw IllegalArgumentException("다른 매장의 예약입니다")
        }

        // TODO: 실제로는 고객 정보, 차량 정보, 서비스 정보를 조회해야 함
        val responseDto = OwnerReservationResponseDto.from(
            reservation = reservation,
            customerName = "고객${reservation.customerMemberId}",
            customerPhone = "010-0000-0000",
            carInfo = "차량 정보",
            services = listOf(
                ServiceDto("서비스명", 1, reservation.finalAmount)
            )
        )

        return ApiResponse.success(responseDto, "예약 상세 조회가 완료되었습니다")
    }

    fun approveReservation(
        storeId: Long,
        reservationId: Long,
        reason: String?
    ): ApiResponse<Nothing> {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 예약입니다") }

        if (reservation.storeId != storeId) {
            throw IllegalArgumentException("다른 매장의 예약입니다")
        }

        val approvedReservation = reservation.confirm()
        reservationRepository.save(approvedReservation)

        return ApiResponse.success("예약이 승인되었습니다")
    }

    fun rejectReservation(
        storeId: Long,
        reservationId: Long,
        reason: String
    ): ApiResponse<Nothing> {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 예약입니다") }

        if (reservation.storeId != storeId) {
            throw IllegalArgumentException("다른 매장의 예약입니다")
        }

        val rejectedReservation = reservation.reject(reason)
        reservationRepository.save(rejectedReservation)

        return ApiResponse.success("예약이 거절되었습니다")
    }

    fun startService(
        storeId: Long,
        reservationId: Long
    ): ApiResponse<Nothing> {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 예약입니다") }

        if (reservation.storeId != storeId) {
            throw IllegalArgumentException("다른 매장의 예약입니다")
        }

        val inProgressReservation = reservation.startService()
        reservationRepository.save(inProgressReservation)

        return ApiResponse.success("서비스가 시작되었습니다")
    }

    fun completeService(
        storeId: Long,
        reservationId: Long
    ): ApiResponse<Nothing> {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 예약입니다") }

        if (reservation.storeId != storeId) {
            throw IllegalArgumentException("다른 매장의 예약입니다")
        }

        val completedReservation = reservation.complete()
        reservationRepository.save(completedReservation)

        return ApiResponse.success("서비스가 완료되었습니다")
    }

    fun markAsNoShow(
        storeId: Long,
        reservationId: Long
    ): ApiResponse<Nothing> {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 예약입니다") }

        if (reservation.storeId != storeId) {
            throw IllegalArgumentException("다른 매장의 예약입니다")
        }

        val noShowReservation = reservation.markAsNoShow()
        reservationRepository.save(noShowReservation)

        return ApiResponse.success("노쇼 처리가 완료되었습니다")
    }

    @Transactional(readOnly = true)
    fun getDailySchedule(
        storeId: Long,
        date: LocalDate
    ): ApiResponse<DailyScheduleResponseDto> {
        val reservations = reservationRepository.findByStoreIdAndReservationDate(storeId, date)
        
        val reservationList = reservations.map { reservation ->
            OwnerReservationListResponseDto(
                id = reservation.id,
                customerName = "고객${reservation.customerMemberId}",
                customerPhone = "010-0000-0000",
                carInfo = "차량 정보",
                reservationDate = reservation.reservationDate,
                reservationTime = reservation.reservationTime,
                status = reservation.status,
                finalAmount = reservation.finalAmount,
                serviceCount = 1,
                isUrgent = isUrgentReservation(reservation.reservationDate, reservation.reservationTime)
            )
        }.sortedBy { it.reservationTime }

        val responseDto = DailyScheduleResponseDto(
            date = date,
            totalReservations = reservations.size,
            pendingCount = reservations.count { it.status == ReservationStatus.PENDING },
            confirmedCount = reservations.count { it.status == ReservationStatus.CONFIRMED },
            inProgressCount = reservations.count { it.status == ReservationStatus.IN_PROGRESS },
            completedCount = reservations.count { it.status == ReservationStatus.COMPLETED },
            reservations = reservationList
        )

        return ApiResponse.success(responseDto, "일별 스케줄 조회가 완료되었습니다")
    }

    @Transactional(readOnly = true)
    fun getReservationSummary(
        storeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): ApiResponse<ReservationSummaryDto> {
        val summaryData = reservationRepository.getReservationSummaryByStore(storeId, startDate, endDate)
        
        var totalReservations = 0L
        var pendingReservations = 0L
        var confirmedReservations = 0L
        var completedReservations = 0L
        
        summaryData.forEach { data ->
            val status = data[0] as ReservationStatus
            val count = data[1] as Long
            
            totalReservations += count
            when (status) {
                ReservationStatus.PENDING -> pendingReservations = count
                ReservationStatus.CONFIRMED -> confirmedReservations = count
                ReservationStatus.COMPLETED -> completedReservations = count
                else -> { /* 다른 상태들 */ }
            }
        }

        // 완료된 예약들의 총 매출 계산
        val completedReservations = reservationRepository.findByStoreIdAndReservationDateBetween(
            storeId, startDate, endDate
        ).filter { it.status == ReservationStatus.COMPLETED }
        
        val totalRevenue = completedReservations.sumOf { it.finalAmount }

        val responseDto = ReservationSummaryDto(
            totalReservations = totalReservations,
            pendingReservations = pendingReservations,
            confirmedReservations = confirmedReservations,
            completedReservations = completedReservations.size.toLong(),
            totalRevenue = totalRevenue.toString()
        )

        return ApiResponse.success(responseDto, "예약 현황 요약 조회가 완료되었습니다")
    }

    private fun isUrgentReservation(reservationDate: LocalDate, reservationTime: LocalTime): Boolean {
        // 예약 시간이 2시간 이내인 경우 긴급으로 표시
        val now = LocalDate.now()
        val currentTime = LocalTime.now()
        
        return when {
            reservationDate.isBefore(now) -> false
            reservationDate.isEqual(now) -> reservationTime.isBefore(currentTime.plusHours(2))
            else -> false
        }
    }
} 