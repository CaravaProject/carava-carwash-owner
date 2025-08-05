package com.carava.carwash.store.entity

/**
 * 매장 카테고리
 */
enum class StoreCategory(
    val displayName: String,
    val description: String
) {
    CAR_WASH("세차장", "기본 세차 서비스를 제공하는 매장"),
    PPF("PPF", "Paint Protection Film 서비스를 제공하는 매장"),
    TINTING("틴팅", "윈도우 틴팅 서비스를 제공하는 매장"),
    COATING("코팅", "차량 코팅 서비스를 제공하는 매장"),
    DETAILING("디테일링", "전문 디테일링 서비스를 제공하는 매장");
    
    companion object {
        fun fromDisplayName(displayName: String): StoreCategory? {
            return values().find { it.displayName == displayName }
        }
    }
} 