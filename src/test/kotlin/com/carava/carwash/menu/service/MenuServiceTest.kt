package com.carava.carwash.menu.service

import com.carava.carwash.menu.dto.CreateMenuRequestDto
import com.carava.carwash.menu.dto.UpdateMenuRequestDto
import com.carava.carwash.menu.dto.UpdateMenuOrderRequestDto
import com.carava.carwash.menu.dto.MenuOrderDto
import com.carava.carwash.menu.entity.Menu
import com.carava.carwash.menu.repository.MenuRepository
import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.entity.StoreCategory
import com.carava.carwash.store.entity.StoreStatus
import com.carava.carwash.store.repository.StoreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MenuServiceTest {

    private val menuRepository = mockk<MenuRepository>()
    private val storeRepository = mockk<StoreRepository>()
    
    private val menuService = MenuService(
        menuRepository = menuRepository,
        storeRepository = storeRepository
    )

    private val testStore = Store(
        id = 1L,
        ownerMemberId = 1L,
        name = "테스트 세차장",
        description = "좋은 세차장",
        phone = "02-1234-5678",
        addressId = 1L,
        category = StoreCategory.CAR_WASH,
        status = StoreStatus.ACTIVE
    )

    @Test
    fun `given_valid_request_when_create_menu_then_success`() {
        // given
        val ownerId = 1L
        val request = CreateMenuRequestDto(
            categoryName = "기본세차",
            name = "실내외 풀세차",
            description = "완벽한 세차 서비스",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\", \"SUV\"]",
            displayOrder = 1
        )

        val savedMenu = Menu(
            id = 1L,
            storeId = testStore.id,
            categoryName = request.categoryName,
            name = request.name,
            description = request.description,
            price = request.price,
            duration = request.duration,
            carTypes = request.carTypes,
            displayOrder = request.displayOrder
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.existsByStoreIdAndName(testStore.id, request.name) } returns false
        every { menuRepository.save(any()) } returns savedMenu

        // when
        val response = menuService.createMenu(ownerId, request)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("메뉴가 성공적으로 등록되었습니다", response.message)
        assertEquals(request.name, response.data?.name)
        assertEquals(request.categoryName, response.data?.categoryName)

        verify { menuRepository.save(any()) }
    }

    @Test
    fun `given_no_store_when_create_menu_then_fail`() {
        // given
        val ownerId = 1L
        val request = CreateMenuRequestDto(
            categoryName = "기본세차",
            name = "실내외 풀세차",
            description = "완벽한 세차 서비스",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\", \"SUV\"]",
            displayOrder = 1
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.empty()

        // when
        val response = menuService.createMenu(ownerId, request)

        // then
        assertFalse(response.success)
        assertEquals("STORE_NOT_FOUND", response.errorCode)
        assertEquals("등록된 매장이 없습니다", response.message)
    }

    @Test
    fun `given_duplicate_menu_name_when_create_menu_then_fail`() {
        // given
        val ownerId = 1L
        val request = CreateMenuRequestDto(
            categoryName = "기본세차",
            name = "중복된 메뉴",
            description = "완벽한 세차 서비스",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\", \"SUV\"]",
            displayOrder = 1
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.existsByStoreIdAndName(testStore.id, request.name) } returns true

        // when
        val response = menuService.createMenu(ownerId, request)

        // then
        assertFalse(response.success)
        assertEquals("MENU_NAME_DUPLICATE", response.errorCode)
        assertEquals("이미 존재하는 메뉴명입니다", response.message)
    }

    @Test
    fun `given_valid_owner_when_get_store_menus_then_success`() {
        // given
        val ownerId = 1L
        val menus = listOf(
            Menu(
                id = 1L,
                storeId = testStore.id,
                categoryName = "기본세차",
                name = "실내외 풀세차",
                description = "완벽한 세차 서비스",
                price = BigDecimal("25000"),
                duration = 60,
                carTypes = "[\"SEDAN\", \"SUV\"]",
                isActive = true,
                displayOrder = 1
            ),
            Menu(
                id = 2L,
                storeId = testStore.id,
                categoryName = "고급세차",
                name = "프리미엄 세차",
                description = "최고급 세차 서비스",
                price = BigDecimal("50000"),
                duration = 120,
                carTypes = "[\"SEDAN\", \"SUV\"]",
                isActive = true,
                displayOrder = 2
            )
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrder(testStore.id) } returns menus

        // when
        val response = menuService.getStoreMenus(ownerId, false)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("메뉴 목록을 성공적으로 조회했습니다", response.message)
        assertEquals(2, response.data?.size)
    }

    @Test
    fun `given_valid_menu_id_when_get_menu_detail_then_success`() {
        // given
        val ownerId = 1L
        val menuId = 1L
        val menu = Menu(
            id = menuId,
            storeId = testStore.id,
            categoryName = "기본세차",
            name = "실내외 풀세차",
            description = "완벽한 세차 서비스",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\", \"SUV\"]",
            isActive = true,
            displayOrder = 1
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findByIdAndStoreId(menuId, testStore.id) } returns Optional.of(menu)

        // when
        val response = menuService.getMenuDetail(ownerId, menuId)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("메뉴 상세 정보를 성공적으로 조회했습니다", response.message)
        assertEquals(menu.name, response.data?.name)
    }

    @Test
    fun `given_invalid_menu_id_when_get_menu_detail_then_fail`() {
        // given
        val ownerId = 1L
        val menuId = 999L

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findByIdAndStoreId(menuId, testStore.id) } returns Optional.empty()

        // when
        val response = menuService.getMenuDetail(ownerId, menuId)

        // then
        assertFalse(response.success)
        assertEquals("MENU_NOT_FOUND", response.errorCode)
        assertEquals("메뉴를 찾을 수 없습니다", response.message)
    }

    @Test
    fun `given_valid_update_request_when_update_menu_then_success`() {
        // given
        val ownerId = 1L
        val menuId = 1L
        val updateRequest = UpdateMenuRequestDto(
            categoryName = "고급세차",
            name = "업데이트된 메뉴",
            description = "새로운 설명",
            price = BigDecimal("30000"),
            duration = 90,
            carTypes = "[\"SEDAN\", \"SUV\", \"TRUCK\"]"
        )

        val existingMenu = Menu(
            id = menuId,
            storeId = testStore.id,
            categoryName = "기본세차",
            name = "기존 메뉴",
            description = "기존 설명",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\"]",
            isActive = true,
            displayOrder = 1
        )

        val updatedMenu = existingMenu.copy().apply {
            updateInfo(
                name = updateRequest.name,
                description = updateRequest.description,
                price = updateRequest.price,
                duration = updateRequest.duration,
                categoryName = updateRequest.categoryName
            )
            carTypes = updateRequest.carTypes
        }

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findByIdAndStoreId(menuId, testStore.id) } returns Optional.of(existingMenu)
        every { menuRepository.existsByStoreIdAndName(testStore.id, updateRequest.name!!) } returns false
        every { menuRepository.save(any()) } returns updatedMenu

        // when
        val response = menuService.updateMenu(ownerId, menuId, updateRequest)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("메뉴가 성공적으로 수정되었습니다", response.message)

        verify { menuRepository.save(any()) }
    }

    @Test
    fun `given_activate_request_when_toggle_menu_status_then_success`() {
        // given
        val ownerId = 1L
        val menuId = 1L
        val activate = true

        val menu = Menu(
            id = menuId,
            storeId = testStore.id,
            categoryName = "기본세차",
            name = "테스트 메뉴",
            description = "설명",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\"]",
            isActive = false,
            displayOrder = 1
        )

        val activatedMenu = menu.copy().apply { activate() }

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findByIdAndStoreId(menuId, testStore.id) } returns Optional.of(menu)
        every { menuRepository.save(any()) } returns activatedMenu

        // when
        val response = menuService.toggleMenuStatus(ownerId, menuId, activate)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("메뉴가 성공적으로 활성화되었습니다", response.message)

        verify { menuRepository.save(any()) }
    }

    @Test
    fun `given_valid_menu_when_delete_menu_then_success`() {
        // given
        val ownerId = 1L
        val menuId = 1L

        val menu = Menu(
            id = menuId,
            storeId = testStore.id,
            categoryName = "기본세차",
            name = "삭제할 메뉴",
            description = "설명",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\"]",
            isActive = true,
            displayOrder = 1
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findByIdAndStoreId(menuId, testStore.id) } returns Optional.of(menu)
        every { menuRepository.delete(menu) } returns Unit

        // when
        val response = menuService.deleteMenu(ownerId, menuId)

        // then
        assertTrue(response.success)
        assertEquals("메뉴가 성공적으로 삭제되었습니다", response.message)

        verify { menuRepository.delete(menu) }
    }

    @Test
    fun `given_valid_order_request_when_update_menu_order_then_success`() {
        // given
        val ownerId = 1L
        val request = UpdateMenuOrderRequestDto(
            menuOrders = listOf(
                MenuOrderDto(menuId = 1L, displayOrder = 2),
                MenuOrderDto(menuId = 2L, displayOrder = 1)
            )
        )

        val menu1 = Menu(
            id = 1L,
            storeId = testStore.id,
            categoryName = "기본세차",
            name = "메뉴1",
            description = "설명1",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\"]",
            isActive = true,
            displayOrder = 1
        )

        val menu2 = Menu(
            id = 2L,
            storeId = testStore.id,
            categoryName = "기본세차",
            name = "메뉴2",
            description = "설명2",
            price = BigDecimal("30000"),
            duration = 90,
            carTypes = "[\"SUV\"]",
            isActive = true,
            displayOrder = 2
        )

        val menus = listOf(menu1, menu2)
        val updatedMenus = listOf(menu2.copy(displayOrder = 1), menu1.copy(displayOrder = 2))

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findAllById(listOf(1L, 2L)) } returns menus
        every { menuRepository.saveAll(any<List<Menu>>()) } returns updatedMenus
        every { menuRepository.findByStoreIdOrderByDisplayOrder(testStore.id) } returns updatedMenus

        // when
        val response = menuService.updateMenuOrder(ownerId, request)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("메뉴 순서가 성공적으로 변경되었습니다", response.message)
        assertEquals(2, response.data?.size)

        verify { menuRepository.saveAll(any<List<Menu>>()) }
    }

    @Test
    fun `given_categories_when_get_store_menus_by_category_then_success`() {
        // given
        val ownerId = 1L
        val categories = listOf("기본세차", "고급세차")
        val basicMenus = listOf(
            Menu(
                id = 1L,
                storeId = testStore.id,
                categoryName = "기본세차",
                name = "기본 메뉴",
                description = "기본 설명",
                price = BigDecimal("25000"),
                duration = 60,
                carTypes = "[\"SEDAN\"]",
                isActive = true,
                displayOrder = 1
            )
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(testStore)
        every { menuRepository.findDistinctCategoryNamesByStoreId(testStore.id) } returns categories
        every { menuRepository.findByStoreIdAndCategoryNameAndIsActiveTrue(testStore.id, "기본세차") } returns basicMenus
        every { menuRepository.findByStoreIdAndCategoryNameAndIsActiveTrue(testStore.id, "고급세차") } returns emptyList()

        // when
        val response = menuService.getStoreMenusByCategory(ownerId)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("카테고리별 메뉴 목록을 성공적으로 조회했습니다", response.message)
        assertEquals(2, response.data?.size)
    }
} 