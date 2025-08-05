package com.carava.carwash.store.controller

import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.store.dto.*
import com.carava.carwash.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/owner/store")
@Tag(name = "사장 매장 관리", description = "사장님을 위한 매장 관리 API")
class StoreController(
    private val storeService: StoreService
) {

    @PostMapping
    @Operation(
        summary = "매장 등록",
        description = "새로운 매장을 등록합니다"
    )
    @SwaggerApiResponse(responseCode = "201", description = "매장 등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "409", description = "매장명 중복 또는 이미 등록된 매장 존재")
    fun createStore(
        @Valid @RequestBody request: CreateStoreRequestDto,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<StoreResponseDto>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = storeService.createStore(ownerId, request)
        
        val status = if (response.success) HttpStatus.CREATED else HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping
    @Operation(
        summary = "내 매장 정보 조회",
        description = "등록된 매장 정보를 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "등록된 매장 없음")
    fun getMyStore(
        authentication: Authentication
    ): ResponseEntity<ApiResponse<StoreResponseDto>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = storeService.getMyStore(ownerId)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @PutMapping
    @Operation(
        summary = "매장 정보 수정",
        description = "매장 기본 정보를 수정합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    @SwaggerApiResponse(responseCode = "409", description = "매장명 중복")
    fun updateStore(
        @Valid @RequestBody request: UpdateStoreRequestDto,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<StoreResponseDto>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = storeService.updateStore(ownerId, request)
        
        val status = when {
            response.success -> HttpStatus.OK
            response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            response.errorCode == "STORE_NAME_DUPLICATE" -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status).body(response)
    }

    @PutMapping("/status")
    @Operation(
        summary = "매장 상태 변경",
        description = "매장을 활성화/비활성화합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "상태 변경 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    fun toggleStoreStatus(
        @Parameter(description = "활성화 여부") @RequestParam activate: Boolean,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<StoreResponseDto>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = storeService.toggleStoreStatus(ownerId, activate)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @DeleteMapping
    @Operation(
        summary = "매장 삭제",
        description = "매장을 삭제합니다 (비활성화)"
    )
    @SwaggerApiResponse(responseCode = "200", description = "삭제 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    fun deleteStore(
        authentication: Authentication
    ): ResponseEntity<ApiResponse<Nothing>> {
        val ownerId = getOwnerIdFromAuth(authentication)
        val response = storeService.deleteStore(ownerId)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/check-name")
    @Operation(
        summary = "매장명 중복 확인",
        description = "매장명 사용 가능 여부를 확인합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "확인 완료")
    fun checkStoreName(
        @Parameter(description = "확인할 매장명") @RequestParam name: String
    ): ResponseEntity<ApiResponse<Boolean>> {
        val response = storeService.checkStoreName(name)
        return ResponseEntity.ok(response)
    }

    private fun getOwnerIdFromAuth(authentication: Authentication): Long {
        // JWT에서 ownerId 추출 (임시로 username을 사용)
        // 실제로는 JWT에서 사장님 ID를 추출해야 함
        return authentication.name.toLongOrNull() 
            ?: throw IllegalArgumentException("유효하지 않은 인증 정보입니다")
    }
} 