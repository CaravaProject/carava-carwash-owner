package com.carava.carwash.menu.controller

import com.carava.carwash.global.annotation.CurrentMemberId
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.menu.dto.CreateMenuRequestDto
import com.carava.carwash.menu.dto.CreateMenuResponseDto
import com.carava.carwash.menu.service.MenuService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("menuController")
@RequestMapping("/api/menus")
class MenuController(
    private val menuService: MenuService,
) {

    @PostMapping
    fun createMenu(
        @Valid @RequestBody request: CreateMenuRequestDto,
        @CurrentMemberId memberId: Long,
    ) : ApiResponse<CreateMenuResponseDto> {
        val response = menuService.createMenu(request, memberId)
        return ApiResponse.success(data = response)
    }

}