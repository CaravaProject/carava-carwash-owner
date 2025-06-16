package com.carava.carwash.auth.controller

import com.carava.carwash.auth.dto.SignInRequestDto
import com.carava.carwash.auth.dto.SignInResponseDto
import com.carava.carwash.auth.dto.SignUpRequestDto
import com.carava.carwash.auth.dto.SignUpResponseDto
import com.carava.carwash.auth.service.AuthService
import com.carava.carwash.global.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun singUp (
        @Valid @RequestBody request: SignUpRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<SignUpResponseDto>> {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(
                    errorCode = "VALIDATION_ERROR"
                )
            )
        }

        val response = authService.signUp(request)
        return if (response.success) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping("/signin")
    fun signIn (
        @Valid @RequestBody request:SignInRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<SignInResponseDto>> {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(
                    errorCode = "VALIDATION_ERROR"
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