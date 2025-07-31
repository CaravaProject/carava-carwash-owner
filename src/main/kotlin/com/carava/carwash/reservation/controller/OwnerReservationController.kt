package com.carava.carwash.reservation.controller

import com.carava.carwash.reservation.dto.*
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.service.OwnerReservationService
import com.carava.carwash.shared.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/owner")
@Tag(name = "사장 예약 관리", description = "사장님을 위한 예약 관리 API")
class OwnerReservationController(
    private val ownerReservationService: OwnerReservationService
) {

    @GetMapping("/reservations")
    @Operation(
        summary = "매장 예약 목록 조회",
        description = "사장님 매장의 예약 목록을 조회합니다. 상태별 필터링 가능"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun getStoreReservations(
        @Parameter(description = "예약 상태 필터") 
        @RequestParam(required = false) status: ReservationStatus?,
        @PageableDefault(size = 20) pageable: Pageable,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Page<OwnerReservationListResponseDto>>> {
        val storeId = getStoreIdFromAuth(authentication)
        
        val response = if (status != null) {
            ownerReservationService.getStoreReservationsByStatus(storeId, status, pageable)
        } else {
            ownerReservationService.getStoreReservations(storeId, pageable)
        }
        
        return ResponseEntity.ok(response)
    }

    @GetMapping("/reservations/{id}")
    @Operation(
        summary = "예약 상세 조회",
        description = "특정 예약의 상세 정보를 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "403", description = "권한 없음")
    @SwaggerApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
    fun getReservationDetail(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<OwnerReservationResponseDto>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.getReservationDetail(storeId, id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/reservations/{id}/approve")
    @Operation(
        summary = "예약 승인",
        description = "대기 중인 예약을 승인합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "승인 성공")
    @SwaggerApiResponse(responseCode = "400", description = "승인할 수 없는 예약")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "403", description = "권한 없음")
    fun approveReservation(
        @PathVariable id: Long,
        @Valid @RequestBody request: ReservationApprovalRequestDto,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.approveReservation(storeId, id, request.reason)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/reservations/{id}/reject")
    @Operation(
        summary = "예약 거절",
        description = "대기 중인 예약을 거절합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "거절 성공")
    @SwaggerApiResponse(responseCode = "400", description = "거절할 수 없는 예약")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "403", description = "권한 없음")
    fun rejectReservation(
        @PathVariable id: Long,
        @Valid @RequestBody request: ReservationApprovalRequestDto,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.rejectReservation(storeId, id, request.reason!!)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/reservations/{id}/start")
    @Operation(
        summary = "서비스 시작",
        description = "승인된 예약의 서비스를 시작합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "시작 성공")
    @SwaggerApiResponse(responseCode = "400", description = "시작할 수 없는 예약")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun startService(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.startService(storeId, id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/reservations/{id}/complete")
    @Operation(
        summary = "서비스 완료",
        description = "진행 중인 서비스를 완료합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "완료 성공")
    @SwaggerApiResponse(responseCode = "400", description = "완료할 수 없는 예약")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun completeService(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.completeService(storeId, id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/reservations/{id}/no-show")
    @Operation(
        summary = "노쇼 처리",
        description = "승인된 예약을 노쇼로 처리합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "노쇼 처리 성공")
    @SwaggerApiResponse(responseCode = "400", description = "노쇼 처리할 수 없는 예약")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun markAsNoShow(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.markAsNoShow(storeId, id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/schedule/{date}")
    @Operation(
        summary = "일별 스케줄 조회",
        description = "특정 날짜의 예약 스케줄을 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun getDailySchedule(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<DailyScheduleResponseDto>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.getDailySchedule(storeId, date)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/reservations/summary")
    @Operation(
        summary = "예약 현황 요약",
        description = "지정된 기간의 예약 현황 요약을 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun getReservationSummary(
        @Parameter(description = "시작 날짜")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @Parameter(description = "종료 날짜")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<ReservationSummaryDto>> {
        val storeId = getStoreIdFromAuth(authentication)
        val response = ownerReservationService.getReservationSummary(storeId, startDate, endDate)
        return ResponseEntity.ok(response)
    }

    private fun getStoreIdFromAuth(authentication: Authentication): Long {
        // JWT에서 storeId 추출 (임시로 username을 사용)
        // 실제로는 JWT에서 사장님의 매장 ID를 추출해야 함
        return authentication.name.toLongOrNull() 
            ?: throw IllegalArgumentException("유효하지 않은 인증 정보입니다")
    }
} 