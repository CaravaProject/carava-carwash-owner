package com.carava.carwash.customer.entity

import com.carava.carwash.global.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity(name = "CustomerAuth")
@Table(name = "customer_auth")
class Auth (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    var email: String,

    @Column(nullable = false)
    var password: String,
) : BaseEntity()