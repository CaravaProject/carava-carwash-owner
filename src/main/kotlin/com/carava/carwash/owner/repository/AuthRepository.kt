package com.carava.carwash.owner.repository

import com.carava.carwash.owner.entity.Auth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("ownerAuthRepository")
interface AuthRepository : JpaRepository<Auth, Long> {
    fun findByEmail(email: String): Optional<Auth>
    fun existsByEmail(email: String): Boolean
}