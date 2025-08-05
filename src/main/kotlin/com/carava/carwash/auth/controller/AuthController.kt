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
    @SwaggerApiResponse(
        responseCode = "201", 
        description = "회원가입 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                value = """
                {
                    "success": true,
                    "data": {
                        "authId": 1,
                        "email": "owner@example.com",
                        "name": "김사장",
                        "businessName": "깨끗한 세차장",
                        "createdAt": "2024-01-01T10:00:00"
                    },
                    "message": "회원가입이 완료되었습니다",
                    "code": null,
                    "errorCode": null
                }
                """
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "400", 
        description = "잘못된 요청",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [
                ExampleObject(
                    name = "유효성 검증 실패",
                    value = """
                    {
                        "success": false,
                        "data": null,
                        "message": "입력값이 올바르지 않습니다",
                        "code": null,
                        "errorCode": "VALIDATION_ERROR"
                    }
                    """
                ),
                ExampleObject(
                    name = "사업자 등록번호 형식 오류",
                    value = """
                    {
                        "success": false,
                        "data": null,
                        "message": "올바른 사업자 등록번호 형식이 아닙니다",
                        "code": null,
                        "errorCode": "INVALID_BUSINESS_NUMBER"
                    }
                    """
                )
            ]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "409", 
        description = "이메일 또는 사업자 등록번호 중복",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [
                ExampleObject(
                    name = "이메일 중복",
                    value = """
                    {
                        "success": false,
                        "data": null,
                        "message": "이미 사용 중인 이메일입니다",
                        "code": null,
                        "errorCode": "EMAIL_ALREADY_EXISTS"
                    }
                    """
                ),
                ExampleObject(
                    name = "사업자 등록번호 중복",
                    value = """
                    {
                        "success": false,
                        "data": null,
                        "message": "이미 등록된 사업자 등록번호입니다",
                        "code": null,
                        "errorCode": "BUSINESS_NUMBER_ALREADY_EXISTS"
                    }
                    """
                )
            ]
        )]
    )
    fun singUp (
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
    @SwaggerApiResponse(
        responseCode = "200", 
        description = "로그인 성공",
        content = [Content(
            mediaType = "application/json",
            examples = [ExampleObject(
                value = """
                {
                    "success": true,
                    "data": {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "authId": 1,
                        "email": "owner@example.com",
                        "expiresIn": 86400
                    },
                    "message": "로그인이 완료되었습니다",
                    "code": null,
                    "errorCode": null
                }
                """
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
                value = """
                {
                    "success": false,
                    "data": null,
                    "message": "이메일과 비밀번호를 입력해주세요",
                    "code": null,
                    "errorCode": "MISSING_CREDENTIALS"
                }
                """
            )]
        )]
    )
    @SwaggerApiResponse(
        responseCode = "401", 
        description = "인증 실패",
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
            examples = [
                ExampleObject(
                    name = "잘못된 비밀번호",
                    value = """
                    {
                        "success": false,
                        "data": null,
                        "message": "이메일 또는 비밀번호가 올바르지 않습니다",
                        "code": null,
                        "errorCode": "INVALID_CREDENTIALS"
                    }
                    """
                ),
                ExampleObject(
                    name = "존재하지 않는 사업자",
                    value = """
                    {
                        "success": false,
                        "data": null,
                        "message": "등록되지 않은 사업자입니다",
                        "code": null,
                        "errorCode": "OWNER_NOT_FOUND"
                    }
                    """
                )
            ]
        )]
    )
    fun signIn (
        @Valid @RequestBody request:SignInRequestDto,
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