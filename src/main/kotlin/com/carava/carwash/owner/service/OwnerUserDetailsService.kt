package com.carava.carwash.owner.service

import com.carava.carwash.owner.repository.AuthRepository
import com.carava.carwash.owner.security.OwnerUserDetails
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class OwnerUserDetailsService (
    private val authRepository: AuthRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val auth = authRepository.findByEmail(username)
            .orElseThrow{ UsernameNotFoundException("owner - 사용자를 찾을 수 없습니다.") }

        return OwnerUserDetails(auth)
    }
}