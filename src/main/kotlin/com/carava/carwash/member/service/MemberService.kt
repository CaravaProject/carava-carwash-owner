package com.carava.carwash.member.service

import com.carava.carwash.auth.entity.Auth
import com.carava.carwash.member.dto.CreateMemberRequestDto
import com.carava.carwash.member.entity.Member
import com.carava.carwash.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("memberService")
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun createMember(auth: Auth, request: CreateMemberRequestDto) : Member {
        val member = Member(
            name = request.name,
            auth = auth
        )

        val savedMember = memberRepository.save(member)

        return savedMember
    }
}