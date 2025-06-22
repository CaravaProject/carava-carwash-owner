package com.carava.carwash.owner.controller

import com.carava.carwash.owner.dto.*
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.owner.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController("ownerAuthController")
@RequestMapping("/api/owner/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody request: SignUpRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<SignUpResponseDto>> {

        val response = authService.signUp(request)
        return if (response.isSuccess) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping("/signin")
    fun signIn(
        @Valid @RequestBody request: SignInRequestDto,
        bindingResult: BindingResult
    ) : ResponseEntity<ApiResponse<SignInResponseDto>> {

        val response = authService.signIn(request)
        return if (response.isSuccess) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }

    @GetMapping("/check-username")
    fun checkUsername(@Valid @RequestParam email: String): ResponseEntity<ApiResponse<CheckUsernameResponseDto>>
            = ResponseEntity.ok(authService.checkUsername(email))


}