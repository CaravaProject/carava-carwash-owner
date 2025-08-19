package com.carava.carwash.global.exception

import com.carava.carwash.global.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException) =
        ResponseEntity.badRequest().body(ApiResponse.error<Nothing>("VALIDATION_ERROR", "입력값이 올바르지 않습니다"))

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(ex: EmailAlreadyExistsException) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error<Nothing>("EMAIL_ALREADY_EXISTS", ex.message ?: "이미 존재하는 이메일입니다"))

    @ExceptionHandler(BusinessNumberAlreadyExistsException::class)
    fun handleBusinessNumberAlreadyExistsException(ex: BusinessNumberAlreadyExistsException) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error<Nothing>("BUSINESS_NUMBER_ALREADY_EXISTS", ex.message ?: "이미 등록된 사업자 등록번호입니다"))

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error<Nothing>("INVALID_CREDENTIALS", "잘못된 인증 정보입니다"))

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(ex: UsernameNotFoundException) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error<Nothing>("USER_NOT_FOUND", "사용자를 찾을 수 없습니다"))

    // === 비즈니스 로직 예외 처리 ===
    
    @ExceptionHandler(StoreNotFoundException::class)
    fun handleStoreNotFoundException(ex: StoreNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error<Nothing>("STORE_NOT_FOUND", ex.message ?: "존재하지 않는 매장입니다"))

    @ExceptionHandler(StoreAlreadyExistsException::class)
    fun handleStoreAlreadyExistsException(ex: StoreAlreadyExistsException) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error<Nothing>("STORE_ALREADY_EXISTS", ex.message ?: "이미 등록된 매장이 있습니다"))

    @ExceptionHandler(MenuNotFoundException::class)
    fun handleMenuNotFoundException(ex: MenuNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error<Nothing>("MENU_NOT_FOUND", ex.message ?: "존재하지 않는 메뉴입니다"))

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleReservationNotFoundException(ex: ReservationNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error<Nothing>("RESERVATION_NOT_FOUND", ex.message ?: "존재하지 않는 예약입니다"))

    @ExceptionHandler(UnauthorizedAccessException::class)
    fun handleUnauthorizedAccessException(ex: UnauthorizedAccessException) =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error<Nothing>("UNAUTHORIZED_ACCESS", ex.message ?: "접근 권한이 없습니다"))

    @ExceptionHandler(InvalidReservationStatusException::class)
    fun handleInvalidReservationStatusException(ex: InvalidReservationStatusException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error<Nothing>("INVALID_RESERVATION_STATUS", ex.message ?: "잘못된 예약 상태입니다"))

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error occurred", ex)
        logger.error("Error class: ${ex.javaClass.name}")
        logger.error("Error message: ${ex.message}")
        ex.printStackTrace()
        return ResponseEntity.internalServerError().body(ApiResponse.error<Nothing>("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다"))
    }
}
