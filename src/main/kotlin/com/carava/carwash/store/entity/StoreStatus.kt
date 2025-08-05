package com.carava.carwash.store.entity

/**
 * 매장 상태
 */
enum class StoreStatus(
    val displayName: String,
    val description: String
) {
    ACTIVE("운영중", "정상적으로 운영 중인 상태"),
    INACTIVE("휴업", "임시 휴업 중인 상태"),
    SUSPENDED("정지", "관리자에 의해 정지된 상태"),
    PENDING("승인대기", "가입 후 승인 대기 중인 상태");
    
    fun isOperational(): Boolean {
        return this == ACTIVE
    }
    
    companion object {
        fun fromDisplayName(displayName: String): StoreStatus? {
            return values().find { it.displayName == displayName }
        }
    }
} 