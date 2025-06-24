package com.carava.carwash.customer.repository

import com.carava.carwash.customer.entity.Auth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("customerAuthRepository")
interface AuthRepository : JpaRepository<Auth, Long> {
    fun findByEmail(email: String): Optional<Auth>
    fun existsByEmail(email: String): Boolean
}