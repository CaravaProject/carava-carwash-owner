package com.carava.carwash.auth.entity

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "auth")
data class Auth (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    private val email: String,

    @Column(nullable = false)
    private val password: String,

    @Enumerated(EnumType.STRING)
    val userType: UserType = UserType.CUSTOMER,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "enabled")
    val enabled: Boolean = true

) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${userType.name}"))
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = email // email을 username으로 사용

    fun getEmail(): String = email
}