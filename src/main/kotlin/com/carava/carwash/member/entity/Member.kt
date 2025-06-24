package com.carava.carwash.member.entity

import com.carava.carwash.global.entity.BaseEntity
import jakarta.persistence.*

@Entity(name = "member")
@Table(name = "owner_member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "owner_auth_id", nullable = false)
    var authId: Long = 0,

    @Column(nullable = false)
    var name: String,
) : BaseEntity()