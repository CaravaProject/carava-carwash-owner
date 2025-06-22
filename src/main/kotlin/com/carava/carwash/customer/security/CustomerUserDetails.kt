package com.carava.carwash.customer.security

import com.carava.carwash.customer.entity.Auth
import com.carava.carwash.global.constants.UserType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomerUserDetails(
    private val auth:Auth
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE" + UserType.CUSTOMER))
    }

    override fun getPassword(): String = auth.password

    override fun getUsername(): String = auth.email
}