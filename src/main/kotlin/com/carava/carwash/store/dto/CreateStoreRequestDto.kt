package com.carava.carwash.store.dto

import com.carava.carwash.store.entity.StoreCategory

data class CreateStoreRequestDto(
    val name: String,
    val category: StoreCategory
)
