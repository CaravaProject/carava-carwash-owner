package com.carava.carwash.auth.service

import com.carava.carwash.auth.repository.AuthRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val authRepository: AuthRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return authRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("사용자를 찾을 수 없습니다: $username") }
    }
}