package com.carava.carwash.reservation.controller

import com.carava.carwash.reservation.dto.*
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.service.OwnerReservationService
import com.carava.carwash.shared.dto.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@WebMvcTest(OwnerReservationController::class)
class OwnerReservationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var ownerReservationService: OwnerReservationService

    @Test
    @WithMockUser(username = "1") // storeId = 1L
    fun `given_store_when_get_reservations_then_return_200`() {
        // given
        val storeId = 1L
        val pageable = PageRequest.of(0, 10)
        val reservationList = PageImpl(emptyList<OwnerReservationListResponseDto>())

        every { ownerReservationService.getStoreReservations(storeId, any()) } returns 
            ApiResponse.success(reservationList, "매장 예약 목록 조회가 완료되었습니다")

        // when & then
        mockMvc.perform(
            get("/owner/reservations")
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.message").value("매장 예약 목록 조회가 완료되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_store_and_status_when_get_reservations_by_status_then_return_200`() {
        // given
        val storeId = 1L
        val status = ReservationStatus.PENDING
        val reservationList = PageImpl(emptyList<OwnerReservationListResponseDto>())

        every { ownerReservationService.getStoreReservationsByStatus(storeId, status, any()) } returns 
            ApiResponse.success(reservationList, "매장 예약 목록 조회가 완료되었습니다")

        // when & then
        mockMvc.perform(
            get("/owner/reservations")
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_reservation_id_when_get_reservation_detail_then_return_200`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        val responseData = OwnerReservationResponseDto(
            id = reservationId,
            customerName = "김고객",
            customerPhone = "010-1234-5678",
            carInfo = "현대 아반떼 (12가3456)",
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("30000"),
            customerRequest = "깨끗하게 부탁드립니다",
            rejectionReason = null,
            estimatedDuration = 60,
            services = listOf(ServiceDto("일반세차", 1, BigDecimal("30000"))),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { ownerReservationService.getReservationDetail(storeId, reservationId) } returns 
            ApiResponse.success(responseData, "예약 상세 조회가 완료되었습니다")

        // when & then
        mockMvc.perform(get("/owner/reservations/{id}", reservationId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(reservationId))
            .andExpect(jsonPath("$.data.customerName").value("김고객"))
            .andExpect(jsonPath("$.data.carInfo").value("현대 아반떼 (12가3456)"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_pending_reservation_when_approve_then_return_200`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        val request = ReservationApprovalRequestDto("예약 승인합니다")

        every { ownerReservationService.approveReservation(storeId, reservationId, request.reason) } returns 
            ApiResponse.success("예약이 승인되었습니다")

        // when & then
        mockMvc.perform(
            put("/owner/reservations/{id}/approve", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약이 승인되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_pending_reservation_when_reject_then_return_200`() {
        // given
        val storeId = 1L
        val reservationId = 1L
        val request = ReservationApprovalRequestDto("예약이 불가능합니다")

        every { ownerReservationService.rejectReservation(storeId, reservationId, request.reason!!) } returns 
            ApiResponse.success("예약이 거절되었습니다")

        // when & then
        mockMvc.perform(
            put("/owner/reservations/{id}/reject", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약이 거절되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_confirmed_reservation_when_start_service_then_return_200`() {
        // given
        val storeId = 1L
        val reservationId = 1L

        every { ownerReservationService.startService(storeId, reservationId) } returns 
            ApiResponse.success("서비스가 시작되었습니다")

        // when & then
        mockMvc.perform(put("/owner/reservations/{id}/start", reservationId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("서비스가 시작되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_in_progress_reservation_when_complete_service_then_return_200`() {
        // given
        val storeId = 1L
        val reservationId = 1L

        every { ownerReservationService.completeService(storeId, reservationId) } returns 
            ApiResponse.success("서비스가 완료되었습니다")

        // when & then
        mockMvc.perform(put("/owner/reservations/{id}/complete", reservationId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("서비스가 완료되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_date_when_get_daily_schedule_then_return_200`() {
        // given
        val storeId = 1L
        val date = LocalDate.now().plusDays(1)
        val responseData = DailyScheduleResponseDto(
            date = date,
            totalReservations = 2,
            pendingCount = 1,
            confirmedCount = 1,
            inProgressCount = 0,
            completedCount = 0,
            reservations = emptyList()
        )

        every { ownerReservationService.getDailySchedule(storeId, date) } returns 
            ApiResponse.success(responseData, "일별 스케줄 조회가 완료되었습니다")

        // when & then
        mockMvc.perform(get("/owner/schedule/{date}", date.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.date").value(date.toString()))
            .andExpect(jsonPath("$.data.totalReservations").value(2))
            .andExpect(jsonPath("$.data.pendingCount").value(1))
            .andExpect(jsonPath("$.data.confirmedCount").value(1))
    }

    @Test
    @WithMockUser(username = "1")
    fun `given_date_range_when_get_summary_then_return_200`() {
        // given
        val storeId = 1L
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val responseData = ReservationSummaryDto(
            totalReservations = 10L,
            pendingReservations = 2L,
            confirmedReservations = 3L,
            completedReservations = 5L,
            totalRevenue = "150000"
        )

        every { ownerReservationService.getReservationSummary(storeId, startDate, endDate) } returns 
            ApiResponse.success(responseData, "예약 현황 요약 조회가 완료되었습니다")

        // when & then
        mockMvc.perform(
            get("/owner/reservations/summary")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalReservations").value(10))
            .andExpect(jsonPath("$.data.totalRevenue").value("150000"))
    }
} 