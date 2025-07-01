package com.carava.carwash.store.controller

import com.carava.carwash.global.annotation.CurrentMemberId
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.store.dto.CreateStoreRequestDto
import com.carava.carwash.store.dto.CreateStoreResponseDto
import com.carava.carwash.store.service.StoreService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("storeController")
@RequestMapping("/api/stores")
class StoreController(
    private val storeService: StoreService,
) {

    @PostMapping
    fun createStore(
        @Valid @RequestBody request: CreateStoreRequestDto,
        @CurrentMemberId memberId: Long,
    ) : ApiResponse<CreateStoreResponseDto> {
        val response = storeService.createStore(request, memberId)
        return ApiResponse.success(data = response)
    }

}