package com.carava.carwash.global.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {

    @Value("\${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeLongEnough}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration:86400000}") // 24시간
    private val expiration: Long = 86400000

    private fun getSigningKey(): Key {
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun getEmailFromToken(token: String): String {
        return getClaims(token).subject
    }

    fun getMemberIdFromToken(token: String): Long {
        // TODO: 실제 구현에서는 JWT에 memberId를 포함하거나, 
        // email로 DB에서 조회하여 memberId를 반환해야 함
        // 현재는 임시로 email을 Long으로 변환 (테스트용)
        val email = getEmailFromToken(token)
        return email.hashCode().toLong().let { if (it < 0) -it else it }
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }
}
