package com.carava.carwash.reservation.service

import com.carava.carwash.reservation.entity.Reservation
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.repository.ReservationRepository
import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.entity.StoreCategory
import com.carava.carwash.store.entity.StoreStatus
import com.carava.carwash.store.repository.StoreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OwnerReservationServiceTest {

    private val reservationRepository = mockk<ReservationRepository>()
    private val storeRepository = mockk<StoreRepository>()
    private val ownerReservationService = OwnerReservationService(reservationRepository, storeRepository)

    private val ownerId = 1L
    private val storeId = 1L
    private val reservationId = 1L

    private lateinit var mockStore: Store
    private lateinit var mockReservation: Reservation

    @BeforeEach
    fun setUp() {
        mockStore = Store(
            id = storeId,
            ownerMemberId = ownerId,
            name = "테스트 세차장",
            category = StoreCategory.CAR_WASH,
            status = StoreStatus.ACTIVE,
            description = "테스트 세차장입니다",
            phone = "02-1234-5678",
            addressId = 1L
        )

        mockReservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = storeId,
            carId = 1L,
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("25000")
        )
    }

    @Test
    @DisplayName("매장 예약 목록 조회 - 성공")
    fun getStoreReservations_Success() {
        // Given
        val pageable = PageRequest.of(0, 10, Sort.by("reservationDate", "reservationTime").descending())
        val reservationsPage = PageImpl(listOf(mockReservation), pageable, 1)

        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findByStoreId(storeId, pageable) } returns reservationsPage

        // When
        val result = ownerReservationService.getStoreReservations(ownerId, storeId, null, null, 0, 10)

        // Then
        assertTrue(result.success)
        assertEquals("예약 목록 조회 완료", result.message)
        assertEquals(1, result.data?.totalElements)
        verify { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) }
        verify { reservationRepository.findByStoreId(storeId, pageable) }
    }

    @Test
    @DisplayName("매장 예약 목록 조회 - 매장 없음")
    fun getStoreReservations_StoreNotFound() {
        // Given
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns null

        // When
        val result = ownerReservationService.getStoreReservations(ownerId, storeId, null, null, 0, 10)

        // Then
        assertFalse(result.success)
        assertEquals("STORE_NOT_FOUND", result.errorCode)
        assertEquals("매장을 찾을 수 없습니다", result.message)
    }

    @Test
    @DisplayName("예약 상세 조회 - 성공")
    fun getReservationDetail_Success() {
        // Given
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(mockReservation)

        // When
        val result = ownerReservationService.getReservationDetail(ownerId, storeId, reservationId)

        // Then
        assertTrue(result.success)
        assertEquals("예약 상세 조회 완료", result.message)
        assertEquals(reservationId, result.data?.id)
        verify { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) }
        verify { reservationRepository.findById(reservationId) }
    }

    @Test
    @DisplayName("예약 상세 조회 - 예약 없음")
    fun getReservationDetail_ReservationNotFound() {
        // Given
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.empty()

        // When
        val result = ownerReservationService.getReservationDetail(ownerId, storeId, reservationId)

        // Then
        assertFalse(result.success)
        assertEquals("RESERVATION_NOT_FOUND", result.errorCode)
        assertEquals("예약을 찾을 수 없습니다", result.message)
    }

    @Test
    @DisplayName("예약 승인 - 성공")
    fun approveReservation_Success() {
        // Given
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(mockReservation)
        every { reservationRepository.save(any()) } returns mockReservation

        // When
        val result = ownerReservationService.approveReservation(ownerId, storeId, reservationId)

        // Then
        assertTrue(result.success)
        assertEquals("예약이 승인되었습니다", result.message)
        assertEquals(ReservationStatus.CONFIRMED, mockReservation.status)
        verify { reservationRepository.save(mockReservation) }
    }

    @Test
    @DisplayName("예약 승인 - 잘못된 상태")
    fun approveReservation_InvalidStatus() {
        // Given
        mockReservation.status = ReservationStatus.CONFIRMED
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(mockReservation)

        // When
        val result = ownerReservationService.approveReservation(ownerId, storeId, reservationId)

        // Then
        assertFalse(result.success)
        assertEquals("INVALID_STATUS", result.errorCode)
        assertTrue(result.message?.contains("대기 중인 예약만 승인할 수 있습니다") == true)
    }

    @Test
    @DisplayName("예약 거절 - 성공")
    fun rejectReservation_Success() {
        // Given
        val reason = "매장 사정으로 인한 예약 취소"
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(mockReservation)
        every { reservationRepository.save(any()) } returns mockReservation

        // When
        val result = ownerReservationService.rejectReservation(ownerId, storeId, reservationId, reason)

        // Then
        assertTrue(result.success)
        assertEquals("예약이 거절되었습니다", result.message)
        assertEquals(ReservationStatus.CANCELLED, mockReservation.status)
        assertEquals(reason, mockReservation.rejectionReason)
        verify { reservationRepository.save(mockReservation) }
    }

    @Test
    @DisplayName("예약 상태 변경 - 서비스 시작")
    fun updateReservationStatus_StartService() {
        // Given
        mockReservation.status = ReservationStatus.CONFIRMED
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(mockReservation)
        every { reservationRepository.save(any()) } returns mockReservation

        // When
        val result = ownerReservationService.updateReservationStatus(
            ownerId, storeId, reservationId, ReservationStatus.IN_PROGRESS
        )

        // Then
        assertTrue(result.success)
        assertEquals("서비스가 시작되었습니다", result.message)
        assertEquals(ReservationStatus.IN_PROGRESS, mockReservation.status)
        verify { reservationRepository.save(mockReservation) }
    }

    @Test
    @DisplayName("예약 상태 변경 - 서비스 완료")
    fun updateReservationStatus_CompleteService() {
        // Given
        mockReservation.status = ReservationStatus.IN_PROGRESS
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(mockReservation)
        every { reservationRepository.save(any()) } returns mockReservation

        // When
        val result = ownerReservationService.updateReservationStatus(
            ownerId, storeId, reservationId, ReservationStatus.COMPLETED
        )

        // Then
        assertTrue(result.success)
        assertEquals("서비스가 완료되었습니다", result.message)
        assertEquals(ReservationStatus.COMPLETED, mockReservation.status)
        verify { reservationRepository.save(mockReservation) }
    }

    @Test
    @DisplayName("예약 현황 요약 조회 - 성공")
    fun getReservationSummary_Success() {
        // Given
        val startDate = LocalDate.now().minusDays(30)
        val endDate = LocalDate.now()
        val todayReservation = Reservation(
            id = 2L,
            customerMemberId = 1L,
            storeId = storeId,
            carId = 1L,
            reservationDate = LocalDate.now(),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.COMPLETED,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("25000")
        )

        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.PENDING) } returns 5L
        every { reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.CONFIRMED) } returns 10L
        every { reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.IN_PROGRESS) } returns 2L
        every { reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.COMPLETED) } returns 25L
        every { reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.CANCELLED) } returns 3L
        every { reservationRepository.countByStoreIdAndStatus(storeId, ReservationStatus.NO_SHOW) } returns 1L
        every { reservationRepository.findByStoreIdAndReservationDate(storeId, LocalDate.now()) } returns listOf(todayReservation)

        // When
        val result = ownerReservationService.getReservationSummary(ownerId, storeId, startDate, endDate)

        // Then
        assertTrue(result.success)
        assertEquals("예약 현황 요약 조회 완료", result.message)
        val summary = result.data!!
        assertEquals(5L, summary.pendingCount)
        assertEquals(10L, summary.confirmedCount)
        assertEquals(2L, summary.inProgressCount)
        assertEquals(25L, summary.completedCount)
        assertEquals(3L, summary.cancelledCount)
        assertEquals(1L, summary.noShowCount)
        assertEquals(46L, summary.totalReservations)
        assertEquals(BigDecimal("25000"), summary.todayRevenue)
    }

    @Test
    @DisplayName("다른 매장의 예약 접근 시도 - 실패")
    fun accessOtherStoreReservation_Fails() {
        // Given
        val otherStoreReservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = 999L, // 다른 매장 ID
            carId = 1L,
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("25000")
        )
        every { storeRepository.findByIdAndOwnerMemberId(storeId, ownerId) } returns mockStore
        every { reservationRepository.findById(reservationId) } returns Optional.of(otherStoreReservation)

        // When
        val result = ownerReservationService.getReservationDetail(ownerId, storeId, reservationId)

        // Then
        assertFalse(result.success)
        assertEquals("RESERVATION_NOT_FOUND", result.errorCode)
        assertEquals("해당 매장의 예약이 아닙니다", result.message)
    }
} 