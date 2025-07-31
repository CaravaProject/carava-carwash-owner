package com.carava.carwash.reservation.service

import com.carava.carwash.reservation.entity.Reservation
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.repository.ReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OwnerReservationServiceTest {

    private val reservationRepository = mockk<ReservationRepository>()
    
    private val ownerReservationService = OwnerReservationService(
        reservationRepository = reservationRepository
    )

    @Test
    fun `given_store_id_when_get_store_reservations_then_return_list`() {
        // given
        val storeId = 1L
        val pageable = PageRequest.of(0, 10)
        
        val reservations = listOf(
            Reservation(
                id = 1L,
                customerMemberId = 1L,
                storeId = storeId,
                carId = 1L,
                reservationDate = LocalDate.now().plusDays(1),
                reservationTime = LocalTime.of(10, 0),
                status = ReservationStatus.PENDING,
                totalAmount = BigDecimal("30000"),
                finalAmount = BigDecimal("30000")
            )
        )

        every { reservationRepository.findByStoreId(storeId, pageable) } returns PageImpl(reservations)

        // when
        val response = ownerReservationService.getStoreReservations(storeId, pageable)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals(1, response.data?.content?.size)
    }

    @Test
    fun `given_pending_reservation_when_approve_reservation_then_success`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        val reason = "예약 승인합니다"
        
        val reservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = storeId,
            carId = 1L,
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("30000")
        )

        val approvedReservation = reservation.confirm()

        every { reservationRepository.findById(reservationId) } returns java.util.Optional.of(reservation)
        every { reservationRepository.save(any()) } returns approvedReservation

        // when
        val response = ownerReservationService.approveReservation(storeId, reservationId, reason)

        // then
        assertTrue(response.success)
        assertEquals("예약이 승인되었습니다", response.message)

        verify { reservationRepository.save(any()) }
    }

    @Test
    fun `given_pending_reservation_when_reject_reservation_then_success`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        val reason = "예약이 불가능합니다"
        
        val reservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = storeId,
            carId = 1L,
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("30000")
        )

        val rejectedReservation = reservation.reject(reason)

        every { reservationRepository.findById(reservationId) } returns java.util.Optional.of(reservation)
        every { reservationRepository.save(any()) } returns rejectedReservation

        // when
        val response = ownerReservationService.rejectReservation(storeId, reservationId, reason)

        // then
        assertTrue(response.success)
        assertEquals("예약이 거절되었습니다", response.message)

        verify { reservationRepository.save(any()) }
    }

    @Test
    fun `given_wrong_store_reservation_when_approve_then_throw_exception`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        val reason = "승인"
        
        val reservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = 999L, // 다른 매장
            carId = 1L,
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("30000")
        )

        every { reservationRepository.findById(reservationId) } returns java.util.Optional.of(reservation)

        // when & then
        assertThrows<IllegalArgumentException> {
            ownerReservationService.approveReservation(storeId, reservationId, reason)
        }
    }

    @Test
    fun `given_confirmed_reservation_when_start_service_then_success`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        
        val reservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = storeId,
            carId = 1L,
            reservationDate = LocalDate.now(),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.CONFIRMED,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("30000")
        )

        val inProgressReservation = reservation.startService()

        every { reservationRepository.findById(reservationId) } returns java.util.Optional.of(reservation)
        every { reservationRepository.save(any()) } returns inProgressReservation

        // when
        val response = ownerReservationService.startService(storeId, reservationId)

        // then
        assertTrue(response.success)
        assertEquals("서비스가 시작되었습니다", response.message)

        verify { reservationRepository.save(any()) }
    }

    @Test
    fun `given_in_progress_reservation_when_complete_service_then_success`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        
        val reservation = Reservation(
            id = reservationId,
            customerMemberId = 1L,
            storeId = storeId,
            carId = 1L,
            reservationDate = LocalDate.now(),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.IN_PROGRESS,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("30000")
        )

        val completedReservation = reservation.complete()

        every { reservationRepository.findById(reservationId) } returns java.util.Optional.of(reservation)
        every { reservationRepository.save(any()) } returns completedReservation

        // when
        val response = ownerReservationService.completeService(storeId, reservationId)

        // then
        assertTrue(response.success)
        assertEquals("서비스가 완료되었습니다", response.message)

        verify { reservationRepository.save(any()) }
    }

    @Test
    fun `given_store_and_date_when_get_daily_schedule_then_return_schedule`() {
        // given
        val storeId = 1L
        val date = LocalDate.now().plusDays(1)
        
        val reservations = listOf(
            Reservation(
                id = 1L,
                customerMemberId = 1L,
                storeId = storeId,
                carId = 1L,
                reservationDate = date,
                reservationTime = LocalTime.of(10, 0),
                status = ReservationStatus.CONFIRMED,
                totalAmount = BigDecimal("30000"),
                finalAmount = BigDecimal("30000")
            ),
            Reservation(
                id = 2L,
                customerMemberId = 2L,
                storeId = storeId,
                carId = 2L,
                reservationDate = date,
                reservationTime = LocalTime.of(14, 0),
                status = ReservationStatus.PENDING,
                totalAmount = BigDecimal("25000"),
                finalAmount = BigDecimal("25000")
            )
        )

        every { reservationRepository.findByStoreIdAndReservationDate(storeId, date) } returns reservations

        // when
        val response = ownerReservationService.getDailySchedule(storeId, date)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals(2, response.data?.totalReservations)
        assertEquals(1, response.data?.pendingCount)
        assertEquals(1, response.data?.confirmedCount)
    }
} 