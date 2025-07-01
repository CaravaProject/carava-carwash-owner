package com.carava.carwash.member.entity

import com.carava.carwash.auth.entity.Auth
import com.carava.carwash.global.entity.BaseEntity
import jakarta.persistence.*

@Entity(name = "member")
@Table(name = "owner_member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @OneToOne
    @JoinColumn(name = "auth_id")
    var auth: Auth,

) : BaseEntity()