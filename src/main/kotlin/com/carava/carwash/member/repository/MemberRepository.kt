package com.carava.carwash.member.repository

import com.carava.carwash.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
}