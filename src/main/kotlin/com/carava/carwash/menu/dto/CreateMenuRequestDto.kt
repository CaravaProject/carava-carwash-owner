package com.carava.carwash.menu.dto

import java.math.BigDecimal

data class CreateMenuRequestDto(
    val storeId: Long,
    val name: String,
    val price: BigDecimal,
)
