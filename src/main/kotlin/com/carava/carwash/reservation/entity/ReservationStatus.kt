package com.carava.carwash.reservation.entity

enum class ReservationStatus {
    PENDING,       // 대기중
    CONFIRMED,     // 승인됨
    IN_PROGRESS,   // 서비스 진행중
    COMPLETED,     // 완료
    CANCELLED,     // 취소됨
    NO_SHOW        // 노쇼
} 