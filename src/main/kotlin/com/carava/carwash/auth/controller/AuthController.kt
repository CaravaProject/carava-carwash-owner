package com.carava.carwash.auth.controller

import com.carava.carwash.auth.dto.SignInRequestDto
import com.carava.carwash.auth.dto.SignInResponseDto
import com.carava.carwash.auth.dto.SignUpRequestDto
import com.carava.carwash.auth.dto.SignUpResponseDto
import com.carava.carwash.auth.service.AuthService
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.global.dto.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "사업자 인증", description = "사업자 인증 관리 API")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    @Operation(
        summary = "사업자 회원가입",
        description = "사업자 회원가입을 처리합니다"
    )
    @SwaggerApiResponse(responseCode = "201", description = "회원가입 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "409", description = "이메일 또는 사업자 등록번호 중복")
    fun signUp(
        @Valid @RequestBody request: SignUpRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<SignUpResponseDto>> {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(
                    errorCode = "VALIDATION_ERROR",
                    message = "입력값이 올바르지 않습니다"
                )
            )
        }

        val response = authService.signUp(request)
        return if (response.success) {
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping("/signin")
    @Operation(
        summary = "사업자 로그인",
        description = "사업자 로그인을 처리하고 JWT 토큰을 발급합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "로그인 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    fun signIn(
        @Valid @RequestBody request: SignInRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<SignInResponseDto>> {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(
                    errorCode = "VALIDATION_ERROR",
                    message = "입력값이 올바르지 않습니다"
                )
            )
        }

        val response = authService.signIn(request)
        return if (response.success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }
}