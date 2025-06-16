package com.carava.carwash.auth.service

import com.carava.carwash.auth.dto.SignInResponseDto
import com.carava.carwash.auth.dto.SignInRequestDto
import com.carava.carwash.auth.dto.SignUpResponseDto
import com.carava.carwash.auth.dto.SignUpRequestDto
import com.carava.carwash.auth.entity.Auth
import com.carava.carwash.auth.repository.AuthRepository
import com.carava.carwash.global.config.security.JwtUtil
import com.carava.carwash.global.dto.ApiResponse
import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
@Transactional
class AuthService(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
){
    fun signUp(request: SignUpRequestDto): ApiResponse<SignUpResponseDto> {
        return try {
            if (authRepository.existsByEmail(request.email)) {
                return ApiResponse.error(
                    errorCode = "EMAIL_ALREADY_EXISTS"
                )
            }

            val auth = Auth(
                email = request.email,
                password = passwordEncoder.encode(request.password)
            )

            val savedAuth = authRepository.save(auth)

            ApiResponse.success(
                data = SignUpResponseDto(
                    authId = savedAuth.id,
                    email = savedAuth.getEmail(),
                    createdAt = savedAuth.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            )
        } catch(e: Exception) {
            ApiResponse.error(
                errorCode = "SIGNUP_ERROR"
            )
        }
    }

    fun signIn(request: SignInRequestDto): ApiResponse<SignInResponseDto> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )

            val auth = authRepository.findByEmail(request.email)
                .orElseThrow{ UsernameNotFoundException("사용자를 찾을 수 없습니다.") }

            val token = jwtUtil.generateToken(auth.getEmail())

            ApiResponse.success(
                data = SignInResponseDto(
                    token = token,
                    authId = auth.id,
                    email = auth.getEmail(),
                    expiresIn = 86400
                )
            )
        } catch (e: BadCredentialsException) {
            ApiResponse.error(
                errorCode = "INVALID_CREDENTIALS"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "SIGNIN_ERROR"
            )
        }
    }

}