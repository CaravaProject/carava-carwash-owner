package com.carava.carwash.shared.exception

import com.carava.carwash.shared.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val firstError = ex.bindingResult.fieldErrors.firstOrNull()
        val errorMessage = firstError?.defaultMessage ?: "입력값이 올바르지 않습니다"
        
        log.warn("Validation error: {}", errorMessage)
        
        return ResponseEntity.badRequest().body(
            ApiResponse.failure(errorMessage, "VALIDATION_ERROR")
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Business logic error: {}", ex.message)
        
        return ResponseEntity.badRequest().body(
            ApiResponse.failure(ex.message ?: "잘못된 요청입니다", "BUSINESS_ERROR")
        )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Invalid state error: {}", ex.message)
        
        return ResponseEntity.badRequest().body(
            ApiResponse.failure(ex.message ?: "처리할 수 없는 상태입니다", "STATE_ERROR")
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Runtime error: {}", ex.message, ex)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.failure("서버 내부 오류가 발생했습니다", "INTERNAL_ERROR")
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unexpected error: {}", ex.message, ex)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.failure("예상치 못한 오류가 발생했습니다", "UNKNOWN_ERROR")
        )
    }
} 