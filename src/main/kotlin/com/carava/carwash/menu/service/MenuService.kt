package com.carava.carwash.menu.service

import com.carava.carwash.menu.dto.*
import com.carava.carwash.menu.entity.Menu
import com.carava.carwash.menu.repository.MenuRepository
import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.store.repository.StoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MenuService(
    private val menuRepository: MenuRepository,
    private val storeRepository: StoreRepository
) {

    /**
     * 메뉴 등록
     */
    @Transactional
    fun createMenu(ownerId: Long, request: CreateMenuRequestDto): ApiResponse<MenuResponseDto> {
        return try {
            // 사장님의 매장 조회
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            // 같은 매장에서 메뉴명 중복 확인
            if (menuRepository.existsByStoreIdAndName(store.id, request.name)) {
                return ApiResponse.error(
                    errorCode = "MENU_NAME_DUPLICATE",
                    message = "이미 존재하는 메뉴명입니다"
                )
            }

            // 메뉴 생성
            val menu = Menu(
                storeId = store.id,
                categoryName = request.categoryName,
                name = request.name,
                description = request.description,
                price = request.price,
                duration = request.duration,
                carTypes = request.carTypes,
                displayOrder = request.displayOrder
            )

            val savedMenu = menuRepository.save(menu)

            ApiResponse.success(
                data = MenuResponseDto.from(savedMenu),
                message = "메뉴가 성공적으로 등록되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_CREATE_FAILED",
                message = "메뉴 등록 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 매장 메뉴 목록 조회
     */
    fun getStoreMenus(ownerId: Long, includeInactive: Boolean = false): ApiResponse<List<MenuListResponseDto>> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val menus = if (includeInactive) {
                menuRepository.findByStoreIdOrderByDisplayOrder(store.id)
            } else {
                menuRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrder(store.id)
            }

            val menuDtos = menus.map { MenuListResponseDto.from(it) }

            ApiResponse.success(
                data = menuDtos,
                message = "메뉴 목록을 성공적으로 조회했습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_LIST_GET_FAILED",
                message = "메뉴 목록 조회 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 카테고리별 메뉴 목록 조회
     */
    fun getStoreMenusByCategory(ownerId: Long): ApiResponse<List<CategoryMenuResponseDto>> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val categories = menuRepository.findDistinctCategoryNamesByStoreId(store.id)
            val categoryMenus = categories.map { category ->
                val menus = menuRepository.findByStoreIdAndCategoryNameAndIsActiveTrue(store.id, category)
                CategoryMenuResponseDto.from(category, menus)
            }

            ApiResponse.success(
                data = categoryMenus,
                message = "카테고리별 메뉴 목록을 성공적으로 조회했습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_CATEGORY_GET_FAILED",
                message = "카테고리별 메뉴 조회 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 메뉴 상세 조회
     */
    fun getMenuDetail(ownerId: Long, menuId: Long): ApiResponse<MenuResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val menu = menuRepository.findByIdAndStoreId(menuId, store.id)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "MENU_NOT_FOUND",
                    message = "메뉴를 찾을 수 없습니다"
                )

            ApiResponse.success(
                data = MenuResponseDto.from(menu),
                message = "메뉴 상세 정보를 성공적으로 조회했습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_DETAIL_GET_FAILED",
                message = "메뉴 상세 조회 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    fun updateMenu(ownerId: Long, menuId: Long, request: UpdateMenuRequestDto): ApiResponse<MenuResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val menu = menuRepository.findByIdAndStoreId(menuId, store.id)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "MENU_NOT_FOUND",
                    message = "수정할 메뉴를 찾을 수 없습니다"
                )

            // 메뉴명 중복 확인 (다른 메뉴와의 중복)
            if (request.name != null && request.name != menu.name) {
                if (menuRepository.existsByStoreIdAndName(store.id, request.name)) {
                    return ApiResponse.error(
                        errorCode = "MENU_NAME_DUPLICATE",
                        message = "이미 존재하는 메뉴명입니다"
                    )
                }
            }

            // 메뉴 정보 업데이트
            menu.updateInfo(
                name = request.name,
                description = request.description,
                price = request.price,
                duration = request.duration,
                categoryName = request.categoryName
            )

            request.carTypes?.let { menu.carTypes = it }

            val updatedMenu = menuRepository.save(menu)

            ApiResponse.success(
                data = MenuResponseDto.from(updatedMenu),
                message = "메뉴가 성공적으로 수정되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_UPDATE_FAILED",
                message = "메뉴 수정 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 메뉴 활성화/비활성화
     */
    @Transactional
    fun toggleMenuStatus(ownerId: Long, menuId: Long, activate: Boolean): ApiResponse<MenuResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val menu = menuRepository.findByIdAndStoreId(menuId, store.id)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "MENU_NOT_FOUND",
                    message = "메뉴를 찾을 수 없습니다"
                )

            // 상태 변경
            if (activate) {
                menu.activate()
            } else {
                menu.deactivate()
            }

            val updatedMenu = menuRepository.save(menu)

            val statusMessage = if (activate) "활성화" else "비활성화"
            ApiResponse.success(
                data = MenuResponseDto.from(updatedMenu),
                message = "메뉴가 성공적으로 ${statusMessage}되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_STATUS_UPDATE_FAILED",
                message = "메뉴 상태 변경 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    fun deleteMenu(ownerId: Long, menuId: Long): ApiResponse<Nothing> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val menu = menuRepository.findByIdAndStoreId(menuId, store.id)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "MENU_NOT_FOUND",
                    message = "삭제할 메뉴를 찾을 수 없습니다"
                )

            menuRepository.delete(menu)

            ApiResponse.success<Nothing>(
                message = "메뉴가 성공적으로 삭제되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_DELETE_FAILED",
                message = "메뉴 삭제 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 메뉴 순서 변경
     */
    @Transactional
    fun updateMenuOrder(ownerId: Long, request: UpdateMenuOrderRequestDto): ApiResponse<List<MenuListResponseDto>> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            // 요청된 메뉴들이 모두 해당 매장의 메뉴인지 확인
            val menuIds = request.menuOrders.map { it.menuId }
            val menus = menuRepository.findAllById(menuIds)
            
            val invalidMenus = menus.filter { it.storeId != store.id }
            if (invalidMenus.isNotEmpty()) {
                return ApiResponse.error(
                    errorCode = "INVALID_MENU_IDS",
                    message = "유효하지 않은 메뉴 ID가 포함되어 있습니다"
                )
            }

            // 순서 업데이트
            request.menuOrders.forEach { menuOrder ->
                val menu = menus.find { it.id == menuOrder.menuId }
                menu?.updateDisplayOrder(menuOrder.displayOrder)
            }

            menuRepository.saveAll(menus)

            // 업데이트된 메뉴 목록 반환
            val updatedMenus = menuRepository.findByStoreIdOrderByDisplayOrder(store.id)
            val menuDtos = updatedMenus.map { MenuListResponseDto.from(it) }

            ApiResponse.success(
                data = menuDtos,
                message = "메뉴 순서가 성공적으로 변경되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_ORDER_UPDATE_FAILED",
                message = "메뉴 순서 변경 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 매장 메뉴 요약 정보 조회
     */
    fun getStoreMenuSummary(ownerId: Long): ApiResponse<StoreMenuSummaryResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            val totalMenuCount = menuRepository.count()
            val activeMenuCount = menuRepository.countByStoreIdAndIsActiveTrue(store.id)
            val categories = menuRepository.findDistinctCategoryNamesByStoreId(store.id)
            
            // 최근 등록된 메뉴 5개
            val recentMenus = menuRepository.findByStoreId(store.id, PageRequest.of(0, 5))
                .content.map { MenuListResponseDto.from(it) }

            val summary = StoreMenuSummaryResponseDto(
                totalMenuCount = totalMenuCount,
                activeMenuCount = activeMenuCount,
                categories = categories,
                recentMenus = recentMenus
            )

            ApiResponse.success(
                data = summary,
                message = "메뉴 요약 정보를 성공적으로 조회했습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "MENU_SUMMARY_GET_FAILED",
                message = "메뉴 요약 조회 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }
} 