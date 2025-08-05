package com.carava.carwash.reservation.controller

import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.reservation.dto.*
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.service.OwnerReservationService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Page
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

    @MockBean
    private lateinit var ownerReservationService: OwnerReservationService

    private val storeId = 1L
    private val reservationId = 1L
    private val ownerId = 1L

    private lateinit var mockReservationList: OwnerReservationListResponseDto
    private lateinit var mockReservationDetail: OwnerReservationResponseDto
    private lateinit var mockSummary: ReservationSummaryDto

    @BeforeEach
    fun setUp() {
        mockReservationList = OwnerReservationListResponseDto(
            id = reservationId,
            customerName = "김고객",
            customerPhone = "010-1234-5678",
            carInfo = "현대 아반떼 (12가3456)",
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            finalAmount = BigDecimal("25000"),
            serviceCount = 2,
            isUrgent = false
        )

        mockReservationDetail = OwnerReservationResponseDto(
            id = reservationId,
            customerName = "김고객",
            customerPhone = "010-1234-5678",
            carInfo = "현대 아반떼 (12가3456)",
            reservationDate = LocalDate.now().plusDays(1),
            reservationTime = LocalTime.of(10, 0),
            status = ReservationStatus.PENDING,
            totalAmount = BigDecimal("30000"),
            finalAmount = BigDecimal("25000"),
            customerRequest = "꼼꼼히 세차 부탁드립니다",
            rejectionReason = null,
            estimatedDuration = 60,
            services = listOf(
                ServiceDto("기본 세차", 1, BigDecimal("15000")),
                ServiceDto("왁싱", 1, BigDecimal("10000"))
            ),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        mockSummary = ReservationSummaryDto(
            pendingCount = 5L,
            confirmedCount = 10L,
            inProgressCount = 2L,
            completedCount = 25L,
            cancelledCount = 3L,
            noShowCount = 1L,
            todayRevenue = BigDecimal("150000"),
            totalReservations = 46L
        )
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 목록 조회 - 성공")
    fun getReservations_Success() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val reservationsPage: Page<OwnerReservationListResponseDto> = PageImpl(listOf(mockReservationList), pageable, 1)
        val response = ApiResponse.success(reservationsPage, "예약 목록 조회 완료")

        every { ownerReservationService.getStoreReservations(ownerId, storeId, null, null, 0, 10) } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations", storeId)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약 목록 조회 완료"))
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.data.content[0].id").value(reservationId))
            .andExpect(jsonPath("$.data.content[0].customerName").value("김고객"))
            .andExpect(jsonPath("$.data.content[0].status").value("PENDING"))
            .andExpect(jsonPath("$.data.totalElements").value(1))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 목록 조회 - 상태 필터링")
    fun getReservations_WithStatusFilter() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val reservationsPage: Page<OwnerReservationListResponseDto> = PageImpl(listOf(mockReservationList), pageable, 1)
        val response = ApiResponse.success(reservationsPage, "예약 목록 조회 완료")

        every { 
            ownerReservationService.getStoreReservations(ownerId, storeId, ReservationStatus.PENDING, null, 0, 10) 
        } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations", storeId)
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].status").value("PENDING"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 목록 조회 - 매장 없음")
    fun getReservations_StoreNotFound() {
        // Given
        val response = ApiResponse.error<Page<OwnerReservationListResponseDto>>("STORE_NOT_FOUND", "매장을 찾을 수 없습니다")

        every { ownerReservationService.getStoreReservations(ownerId, storeId, null, null, 0, 10) } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations", storeId)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("STORE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("매장을 찾을 수 없습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 상세 조회 - 성공")
    fun getReservation_Success() {
        // Given
        val response = ApiResponse.success(mockReservationDetail, "예약 상세 조회 완료")

        every { ownerReservationService.getReservationDetail(ownerId, storeId, reservationId) } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations/{reservationId}", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약 상세 조회 완료"))
            .andExpect(jsonPath("$.data.id").value(reservationId))
            .andExpect(jsonPath("$.data.customerName").value("김고객"))
            .andExpect(jsonPath("$.data.customerRequest").value("꼼꼼히 세차 부탁드립니다"))
            .andExpect(jsonPath("$.data.services").isArray)
            .andExpect(jsonPath("$.data.services[0].serviceName").value("기본 세차"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 상세 조회 - 예약 없음")
    fun getReservation_NotFound() {
        // Given
        val response = ApiResponse.error<OwnerReservationResponseDto>("RESERVATION_NOT_FOUND", "예약을 찾을 수 없습니다")

        every { ownerReservationService.getReservationDetail(ownerId, storeId, reservationId) } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations/{reservationId}", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("RESERVATION_NOT_FOUND"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 승인 - 성공")
    fun approveReservation_Success() {
        // Given
        val response = ApiResponse.success<Nothing>(message = "예약이 승인되었습니다")

        every { ownerReservationService.approveReservation(ownerId, storeId, reservationId) } returns response

        // When & Then
        mockMvc.perform(
            put("/owner/stores/{storeId}/reservations/{reservationId}/approve", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약이 승인되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 승인 - 잘못된 상태")
    fun approveReservation_InvalidStatus() {
        // Given
        val response = ApiResponse.error<Nothing>("INVALID_STATUS", "대기 중인 예약만 승인할 수 있습니다")

        every { ownerReservationService.approveReservation(ownerId, storeId, reservationId) } returns response

        // When & Then
        mockMvc.perform(
            put("/owner/stores/{storeId}/reservations/{reservationId}/approve", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("INVALID_STATUS"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 거절 - 성공")
    fun rejectReservation_Success() {
        // Given
        val reason = "매장 사정으로 인한 예약 거절"
        val request = RejectReservationRequestDto(reason)
        val response = ApiResponse.success<Nothing>(message = "예약이 거절되었습니다")

        every { ownerReservationService.rejectReservation(ownerId, storeId, reservationId, reason) } returns response

        // When & Then
        mockMvc.perform(
            put("/owner/stores/{storeId}/reservations/{reservationId}/reject", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약이 거절되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 거절 - 유효성 검증 실패 (빈 사유)")
    fun rejectReservation_EmptyReason() {
        // Given
        val request = RejectReservationRequestDto("")

        // When & Then
        mockMvc.perform(
            put("/owner/stores/{storeId}/reservations/{reservationId}/reject", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 상태 변경 - 서비스 시작")
    fun updateReservationStatus_StartService() {
        // Given
        val request = UpdateReservationStatusRequestDto("IN_PROGRESS")
        val response = ApiResponse.success<Nothing>(message = "서비스가 시작되었습니다")

        every { 
            ownerReservationService.updateReservationStatus(ownerId, storeId, reservationId, ReservationStatus.IN_PROGRESS) 
        } returns response

        // When & Then
        mockMvc.perform(
            put("/owner/stores/{storeId}/reservations/{reservationId}/status", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("서비스가 시작되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 상태 변경 - 서비스 완료")
    fun updateReservationStatus_CompleteService() {
        // Given
        val request = UpdateReservationStatusRequestDto("COMPLETED")
        val response = ApiResponse.success<Nothing>(message = "서비스가 완료되었습니다")

        every { 
            ownerReservationService.updateReservationStatus(ownerId, storeId, reservationId, ReservationStatus.COMPLETED) 
        } returns response

        // When & Then
        mockMvc.perform(
            put("/owner/stores/{storeId}/reservations/{reservationId}/status", storeId, reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("서비스가 완료되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("예약 현황 요약 조회 - 성공")
    fun getReservationSummary_Success() {
        // Given
        val startDate = LocalDate.now().minusDays(30)
        val endDate = LocalDate.now()
        val response = ApiResponse.success(mockSummary, "예약 현황 요약 조회 완료")

        every { ownerReservationService.getReservationSummary(ownerId, storeId, startDate, endDate) } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations/summary", storeId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약 현황 요약 조회 완료"))
            .andExpect(jsonPath("$.data.pendingCount").value(5))
            .andExpect(jsonPath("$.data.confirmedCount").value(10))
            .andExpect(jsonPath("$.data.inProgressCount").value(2))
            .andExpect(jsonPath("$.data.completedCount").value(25))
            .andExpect(jsonPath("$.data.cancelledCount").value(3))
            .andExpect(jsonPath("$.data.noShowCount").value(1))
            .andExpect(jsonPath("$.data.totalReservations").value(46))
            .andExpect(jsonPath("$.data.todayRevenue").value(150000))
    }

    @Test
    @WithMockUser(username = "1") 
    @DisplayName("예약 현황 요약 조회 - 매장 없음")
    fun getReservationSummary_StoreNotFound() {
        // Given
        val startDate = LocalDate.now().minusDays(30)
        val endDate = LocalDate.now()
        val response = ApiResponse.error<ReservationSummaryDto>("STORE_NOT_FOUND", "매장을 찾을 수 없습니다")

        every { ownerReservationService.getReservationSummary(ownerId, storeId, startDate, endDate) } returns response

        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations/summary", storeId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("STORE_NOT_FOUND"))
    }

    @Test
    @DisplayName("인증되지 않은 사용자 접근 - 실패")
    fun unauthorizedAccess_Fails() {
        // When & Then
        mockMvc.perform(
            get("/owner/stores/{storeId}/reservations", storeId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }
} 