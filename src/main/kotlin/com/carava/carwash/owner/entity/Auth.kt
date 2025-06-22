package com.carava.carwash.owner.entity

import jakarta.persistence.*

@Entity(name = "OwnerAuth")
@Table(name = "owner_auth")
class Auth (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    private val email: String,

    @Column(nullable = false)
    private val password: String,
)
