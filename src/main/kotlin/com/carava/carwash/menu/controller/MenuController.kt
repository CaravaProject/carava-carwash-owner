package com.carava.carwash.menu.controller

import com.carava.carwash.global.annotation.CurrentMemberId
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.menu.dto.*
import com.carava.carwash.menu.service.MenuService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/owner/menu")
@Tag(name = "사장 메뉴 관리", description = "사장님을 위한 메뉴 관리 API")
class MenuController(
    private val menuService: MenuService
) {

    @PostMapping
    @Operation(
        summary = "메뉴 등록",
        description = "새로운 메뉴를 등록합니다"
    )
    @SwaggerApiResponse(responseCode = "201", description = "메뉴 등록 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    @SwaggerApiResponse(responseCode = "409", description = "메뉴명 중복")
    fun createMenu(
        @Valid @RequestBody request: CreateMenuRequestDto,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<MenuResponseDto>> {
        val response = menuService.createMenu(ownerId, request)
        
        val status = when {
            response.success -> HttpStatus.CREATED
            response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            response.errorCode == "MENU_NAME_DUPLICATE" -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping
    @Operation(
        summary = "매장 메뉴 목록 조회",
        description = "매장의 메뉴 목록을 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    fun getStoreMenus(
        @Parameter(description = "비활성 메뉴 포함 여부") @RequestParam(defaultValue = "false") includeInactive: Boolean,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<List<MenuListResponseDto>>> {

        val response = menuService.getStoreMenus(ownerId, includeInactive)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/categories")
    @Operation(
        summary = "카테고리별 메뉴 목록 조회",
        description = "카테고리별로 그룹화된 메뉴 목록을 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    fun getStoreMenusByCategory(
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<List<CategoryMenuResponseDto>>> {

        val response = menuService.getStoreMenusByCategory(ownerId)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "메뉴 상세 조회",
        description = "특정 메뉴의 상세 정보를 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    fun getMenuDetail(
        @PathVariable id: Long,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<MenuResponseDto>> {

        val response = menuService.getMenuDetail(ownerId, id)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "메뉴 수정",
        description = "메뉴 정보를 수정합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "수정 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    @SwaggerApiResponse(responseCode = "409", description = "메뉴명 중복")
    fun updateMenu(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateMenuRequestDto,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<MenuResponseDto>> {

        val response = menuService.updateMenu(ownerId, id, request)
        
        val status = when {
            response.success -> HttpStatus.OK
            response.errorCode == "MENU_NOT_FOUND" || response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            response.errorCode == "MENU_NAME_DUPLICATE" -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status).body(response)
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "메뉴 상태 변경",
        description = "메뉴를 활성화/비활성화합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "상태 변경 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    fun toggleMenuStatus(
        @PathVariable id: Long,
        @Parameter(description = "활성화 여부") @RequestParam activate: Boolean,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<MenuResponseDto>> {

        val response = menuService.toggleMenuStatus(ownerId, id, activate)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "메뉴 삭제",
        description = "메뉴를 삭제합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "삭제 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    fun deleteMenu(
        @PathVariable id: Long,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<Nothing>> {

        val response = menuService.deleteMenu(ownerId, id)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }

    @PutMapping("/order")
    @Operation(
        summary = "메뉴 순서 변경",
        description = "메뉴들의 표시 순서를 변경합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "순서 변경 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 요청")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    fun updateMenuOrder(
        @Valid @RequestBody request: UpdateMenuOrderRequestDto,
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<List<MenuListResponseDto>>> {

        val response = menuService.updateMenuOrder(ownerId, request)
        
        val status = when {
            response.success -> HttpStatus.OK
            response.errorCode == "STORE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status).body(response)
    }

    @GetMapping("/summary")
    @Operation(
        summary = "매장 메뉴 요약 정보 조회",
        description = "매장 메뉴의 요약 정보를 조회합니다"
    )
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    @SwaggerApiResponse(responseCode = "401", description = "인증 실패")
    @SwaggerApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    fun getStoreMenuSummary(
        @CurrentMemberId ownerId: Long
    ): ResponseEntity<ApiResponse<StoreMenuSummaryResponseDto>> {

        val response = menuService.getStoreMenuSummary(ownerId)
        
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(response)
    }


} 