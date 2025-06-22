package com.carava.carwash.customer.service

import com.carava.carwash.customer.repository.AuthRepository
import com.carava.carwash.customer.security.CustomerUserDetails
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomerUserDetailsService (
    private val authRepository: AuthRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val auth = authRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("customer - 사용자를 찾을 수 없습니다.") }

        return CustomerUserDetails(auth)
    }
}