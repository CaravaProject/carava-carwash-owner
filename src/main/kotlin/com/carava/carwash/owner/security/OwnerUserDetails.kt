package com.carava.carwash.owner.security

import com.carava.carwash.global.constants.UserType
import com.carava.carwash.owner.entity.Auth
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class OwnerUserDetails(
    private val auth:Auth
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE" + UserType.OWNER))
    }

    override fun getPassword(): String = auth.password

    override fun getUsername(): String = auth.email
}