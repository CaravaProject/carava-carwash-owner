package com.carava.carwash.owner.entity

import com.carava.carwash.global.entity.BaseEntity
import jakarta.persistence.*

@Entity(name = "OwnerAuth")
@Table(name = "owner_auth")
class Auth (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    var email: String,

    @Column(nullable = false)
    var password: String,
) : BaseEntity()
