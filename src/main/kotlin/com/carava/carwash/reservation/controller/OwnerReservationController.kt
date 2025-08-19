package com.carava.carwash.reservation.controller

import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.global.dto.ErrorResponse
import com.carava.carwash.global.exception.UnauthorizedAccessException
import com.carava.carwash.reservation.dto.*
import com.carava.carwash.reservation.entity.ReservationStatus
import com.carava.carwash.reservation.service.OwnerReservationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/owner/stores/{storeId}/reservations")
@Tag(name = "사업자 예약 관리", description = "사업자를 위한 예약 관리 API")
class OwnerReservationController(
    private val ownerReservationService: OwnerReservationService
) {

    @GetMapping
    @Operation(
        summary = "예약 목록 조회",
        description = "매장의 예약 목록을 조회합니다 (페이징 지원)"
    )
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "예약 목록 조회 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                name = "성공 응답",
                value = """
                {
                    "success": true,
                    "data": {
                        "content": [
                            {
                                "id": 1,
                                "customerName": "김고객",
                                "customerPhone": "010-1234-5678",
                                "carInfo": "현대 아반떼 (12가3456)",
                                "reservationDate": "2024-08-01",
                                "reservationTime": "10:00",
                                "status": "PENDING",
                                "finalAmount": 25000,
                                "serviceCount": 2,
                                "isUrgent": false
                            }
                        ],
                        "totalElements": 1,
                        "totalPages": 1,
                        "size": 10,
                        "number": 0
                    },
                    "message": "예약 목록 조회 완료"
                }
                """
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "404", 
        description = "매장을 찾을 수 없음",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [ExampleObject(
                name = "매장 없음",
                value = """{"success": false, "data": null, "message": "매장을 찾을 수 없습니다", "code": null, "errorCode": "STORE_NOT_FOUND"}"""
            )]
        )]
    )
    fun getReservations(
        @PathVariable storeId: Long,
        @Parameter(description = "예약 상태 필터") @RequestParam(required = false) status: ReservationStatus?,
        @Parameter(description = "날짜 필터 (YYYY-MM-DD)") @RequestParam(required = false) date: LocalDate?,
        @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") size: Int,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Page<OwnerReservationListResponseDto>>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = ownerReservationService.getStoreReservations(ownerId, storeId, status, date, page, size)
        
        val httpStatus = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(httpStatus).body(response)
    }

    @GetMapping("/{reservationId}")
    @Operation(
        summary = "예약 상세 조회",
        description = "특정 예약의 상세 정보를 조회합니다"
    )
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "예약 상세 조회 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                name = "성공 응답",
                value = """
                {
                    "success": true,
                    "data": {
                        "id": 1,
                        "customerName": "김고객",
                        "customerPhone": "010-1234-5678",
                        "carInfo": "현대 아반떼 (12가3456)",
                        "reservationDate": "2024-08-01",
                        "reservationTime": "10:00",
                        "status": "PENDING",
                        "totalAmount": 30000,
                        "finalAmount": 25000,
                        "customerRequest": "꼼꼼히 세차 부탁드립니다",
                        "rejectionReason": null,
                        "estimatedDuration": 60,
                        "services": [
                            {"serviceName": "기본 세차", "quantity": 1, "price": 15000},
                            {"serviceName": "왁싱", "quantity": 1, "price": 10000}
                        ],
                        "createdAt": "2024-08-01T09:00:00",
                        "updatedAt": "2024-08-01T09:00:00"
                    },
                    "message": "예약 상세 조회 완료"
                }
                """
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "404", 
        description = "예약을 찾을 수 없음",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [ExampleObject(
                name = "예약 없음",
                value = """{"success": false, "data": null, "message": "예약을 찾을 수 없습니다", "code": null, "errorCode": "RESERVATION_NOT_FOUND"}"""
            )]
        )]
    )
    fun getReservation(
        @PathVariable storeId: Long,
        @PathVariable reservationId: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<OwnerReservationResponseDto>> {
        val ownerId = getOwnerIdFromAuth(authentication) 
        val response = ownerReservationService.getReservationDetail(ownerId, storeId, reservationId)
        
        val httpStatus = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(httpStatus).body(response)
    }

    @PutMapping("/{reservationId}/approve")
    @Operation(
        summary = "예약 승인",
        description = "대기 중인 예약을 승인합니다"
    )
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "예약 승인 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                name = "승인 성공",
                value = """{"success": true, "data": null, "message": "예약이 승인되었습니다"}"""
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "400", 
        description = "잘못된 예약 상태",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [ExampleObject(
                name = "상태 오류",
                value = """{"success": false, "data": null, "message": "대기 중인 예약만 승인할 수 있습니다", "code": null, "errorCode": "INVALID_STATUS"}"""
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "404", 
        description = "예약을 찾을 수 없음",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [ExampleObject(
                name = "예약 없음",
                value = """{"success": false, "data": null, "message": "예약을 찾을 수 없습니다", "code": null, "errorCode": "RESERVATION_NOT_FOUND"}"""
            )]
        )]
    )
    fun approveReservation(
        @PathVariable storeId: Long,
        @PathVariable reservationId: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = ownerReservationService.approveReservation(ownerId, storeId, reservationId)
        
        val httpStatus = when {
            response.success -> HttpStatus.OK
            response.errorCode == "RESERVATION_NOT_FOUND" || response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(httpStatus).body(response)
    }

    @PutMapping("/{reservationId}/reject")
    @Operation(
        summary = "예약 거절",
        description = "대기 중인 예약을 거절합니다"
    )
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "예약 거절 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                name = "거절 성공",
                value = """{"success": true, "data": null, "message": "예약이 거절되었습니다"}"""
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "400", 
        description = "잘못된 요청",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [ExampleObject(
                name = "유효성 검증 실패",
                value = """{"success": false, "data": null, "message": "거절 사유를 입력해주세요", "code": null, "errorCode": "VALIDATION_ERROR"}"""
            )]
        )]
    )
    fun rejectReservation(
        @PathVariable storeId: Long,
        @PathVariable reservationId: Long,
        @Valid @RequestBody request: RejectReservationRequestDto,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = ownerReservationService.rejectReservation(ownerId, storeId, reservationId, request.reason)
        
        val httpStatus = when {
            response.success -> HttpStatus.OK
            response.errorCode == "RESERVATION_NOT_FOUND" || response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(httpStatus).body(response)
    }

    @PutMapping("/{reservationId}/status")
    @Operation(
        summary = "예약 상태 변경",
        description = "예약 상태를 변경합니다 (서비스 시작, 완료, 노쇼 등)"
    )
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "상태 변경 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                name = "서비스 시작",
                value = """{"success": true, "data": null, "message": "서비스가 시작되었습니다"}"""
            )]
        )]
    )
    fun updateReservationStatus(
        @PathVariable storeId: Long,
        @PathVariable reservationId: Long,
        @Valid @RequestBody request: UpdateReservationStatusRequestDto,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val status = ReservationStatus.valueOf(request.status)
        val response = ownerReservationService.updateReservationStatus(ownerId, storeId, reservationId, status)
        
        val httpStatus = when {
            response.success -> HttpStatus.OK
            response.errorCode == "RESERVATION_NOT_FOUND" || response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(httpStatus).body(response)
    }

    @GetMapping("/summary")
    @Operation(
        summary = "예약 현황 요약",
        description = "매장의 예약 현황을 요약해서 조회합니다"
    )
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "예약 현황 조회 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                name = "현황 요약",
                value = """
                {
                    "success": true,
                    "data": {
                        "pendingCount": 5,
                        "confirmedCount": 10,
                        "inProgressCount": 2,
                        "completedCount": 25,
                        "cancelledCount": 3,
                        "noShowCount": 1,
                        "todayRevenue": 150000,
                        "totalReservations": 46
                    },
                    "message": "예약 현황 요약 조회 완료"
                }
                """
            )]
        )]
    )
    fun getReservationSummary(
        @PathVariable storeId: Long,
        @Parameter(description = "시작 날짜") @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") startDate: LocalDate,
        @Parameter(description = "종료 날짜") @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") endDate: LocalDate,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<ReservationSummaryDto>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = ownerReservationService.getReservationSummary(ownerId, storeId, startDate, endDate)
        
        val httpStatus = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(httpStatus).body(response)
    }

    private fun getOwnerIdFromAuth(authentication: Authentication): Long {
        // JWT에서 ownerId 추출 (임시로 username을 사용)
        // 실제로는 JWT에서 사장님 ID를 추출해야 함
        return authentication.name.toLongOrNull() 
            ?: throw UnauthorizedAccessException("유효하지 않은 인증 정보입니다")
    }
} 