package com.carava.carwash.store.controller

import com.carava.carwash.global.annotation.CurrentMemberId
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.store.dto.*
import com.carava.carwash.store.service.HolidayService
import com.carava.carwash.store.service.OperatingHourService
import com.carava.carwash.store.service.StoreService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("storeController")
@RequestMapping("/api/stores")
class StoreController(
    private val storeService: StoreService,
    private val operatingHourService: OperatingHourService,
    private val holidayService: HolidayService,
) {

    @PostMapping
    fun createStore(
        @Valid @RequestBody request: CreateStoreRequestDto,
        @CurrentMemberId memberId: Long,
    ) : ApiResponse<CreateStoreResponseDto> {
        val response = storeService.createStore(request, memberId)
        return ApiResponse.success(data = response)
    }

    @PostMapping("/{storeId}/operating-hours")
    fun saveOperatingHours(
        @PathVariable storeId: Long,
        @RequestBody request: SaveOperatingHourRequestDto,
        @CurrentMemberId memberId: Long
    ) : ApiResponse<SaveOperatingHourResponseDto> {
        val response = operatingHourService.saveOperatingHour(storeId, request, memberId)
        return ApiResponse.success(data = response)
    }

    @PostMapping("/{storeId}/holidays")
    fun createHoliday(
        @PathVariable storeId: Long,
        @RequestBody request: CreateHolidayRequestDto,
        @CurrentMemberId memberId: Long
    ) : ApiResponse<CreateHolidayResponseDto> {
        val response = holidayService.createHoliday(storeId, request, memberId)
        return ApiResponse.success(data = response)
    }

}