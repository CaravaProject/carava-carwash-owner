package com.carava.carwash.menu.dto

import java.time.LocalDateTime

data class CreateMenuResponseDto(
    val menuId: Long,
    val name: String,
    val createdAt: LocalDateTime,
)
