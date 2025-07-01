package com.carava.carwash.auth.entity

import com.carava.carwash.global.entity.BaseEntity
import com.carava.carwash.member.entity.Member
import jakarta.persistence.*

@Entity(name = "auth")
@Table(name = "owner_auth")
class Auth (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false, length = 50)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @OneToOne(mappedBy = "auth")
    var member: Member? = null,

) : BaseEntity() {

    fun getMemberId() : Long {
        return member?.id ?: throw IllegalStateException("Auth에 연결된 Member가 없습니다.")
    }
}
