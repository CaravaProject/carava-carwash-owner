package com.carava.carwash.menu.controller

import com.carava.carwash.menu.dto.*
import com.carava.carwash.menu.service.MenuService
import com.carava.carwash.global.dto.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDateTime

@WebMvcTest(MenuController::class)
class MenuControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var menuService: MenuService

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun menuService(): MenuService = mockk()
    }

    @Test
    @WithMockUser(username = "1")
    fun `POST_owner_menu_should_create_menu_successfully`() {
        // given
        val request = CreateMenuRequestDto(
            categoryName = "기본세차",
            name = "실내외 풀세차",
            description = "완벽한 세차 서비스",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\", \"SUV\"]",
            displayOrder = 1
        )

        val response = ApiResponse.success(
            data = MenuResponseDto(
                id = 1L,
                storeId = 1L,
                categoryName = request.categoryName,
                name = request.name,
                description = request.description,
                price = request.price,
                duration = request.duration,
                carTypes = request.carTypes,
                isActive = true,
                displayOrder = request.displayOrder,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            message = "메뉴가 성공적으로 등록되었습니다"
        )

        every { menuService.createMenu(1L, request) } returns response

        // when & then
        mockMvc.perform(
            post("/owner/menu")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value(request.name))
            .andExpect(jsonPath("$.data.categoryName").value(request.categoryName))
            .andExpect(jsonPath("$.message").value("메뉴가 성공적으로 등록되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `POST_owner_menu_should_return_conflict_when_duplicate_name`() {
        // given
        val request = CreateMenuRequestDto(
            categoryName = "기본세차",
            name = "중복된 메뉴",
            description = "완벽한 세차 서비스",
            price = BigDecimal("25000"),
            duration = 60,
            carTypes = "[\"SEDAN\", \"SUV\"]",
            displayOrder = 1
        )

        val response = ApiResponse.error<MenuResponseDto>(
            errorCode = "MENU_NAME_DUPLICATE",
            message = "이미 존재하는 메뉴명입니다"
        )

        every { menuService.createMenu(1L, request) } returns response

        // when & then
        mockMvc.perform(
            post("/owner/menu")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("MENU_NAME_DUPLICATE"))
            .andExpect(jsonPath("$.message").value("이미 존재하는 메뉴명입니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_menu_should_return_menu_list_successfully`() {
        // given
        val menuList = listOf(
            MenuListResponseDto(
                id = 1L,
                categoryName = "기본세차",
                name = "실내외 풀세차",
                price = BigDecimal("25000"),
                duration = 60,
                isActive = true,
                displayOrder = 1
            ),
            MenuListResponseDto(
                id = 2L,
                categoryName = "고급세차",
                name = "프리미엄 세차",
                price = BigDecimal("50000"),
                duration = 120,
                isActive = true,
                displayOrder = 2
            )
        )

        val response = ApiResponse.success(
            data = menuList,
            message = "메뉴 목록을 성공적으로 조회했습니다"
        )

        every { menuService.getStoreMenus(1L, false) } returns response

        // when & then
        mockMvc.perform(
            get("/owner/menu")
                .param("includeInactive", "false")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].name").value("실내외 풀세차"))
            .andExpect(jsonPath("$.message").value("메뉴 목록을 성공적으로 조회했습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_menu_categories_should_return_category_menus_successfully`() {
        // given
        val categoryMenus = listOf(
            CategoryMenuResponseDto(
                categoryName = "기본세차",
                menus = listOf(
                    MenuListResponseDto(
                        id = 1L,
                        categoryName = "기본세차",
                        name = "실내외 풀세차",
                        price = BigDecimal("25000"),
                        duration = 60,
                        isActive = true,
                        displayOrder = 1
                    )
                )
            ),
            CategoryMenuResponseDto(
                categoryName = "고급세차",
                menus = listOf(
                    MenuListResponseDto(
                        id = 2L,
                        categoryName = "고급세차",
                        name = "프리미엄 세차",
                        price = BigDecimal("50000"),
                        duration = 120,
                        isActive = true,
                        displayOrder = 1
                    )
                )
            )
        )

        val response = ApiResponse.success(
            data = categoryMenus,
            message = "카테고리별 메뉴 목록을 성공적으로 조회했습니다"
        )

        every { menuService.getStoreMenusByCategory(1L) } returns response

        // when & then
        mockMvc.perform(get("/owner/menu/categories"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].categoryName").value("기본세차"))
            .andExpect(jsonPath("$.message").value("카테고리별 메뉴 목록을 성공적으로 조회했습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_menu_id_should_return_menu_detail_successfully`() {
        // given
        val menuId = 1L
        val response = ApiResponse.success(
            data = MenuResponseDto(
                id = menuId,
                storeId = 1L,
                categoryName = "기본세차",
                name = "실내외 풀세차",
                description = "완벽한 세차 서비스",
                price = BigDecimal("25000"),
                duration = 60,
                carTypes = "[\"SEDAN\", \"SUV\"]",
                isActive = true,
                displayOrder = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            message = "메뉴 상세 정보를 성공적으로 조회했습니다"
        )

        every { menuService.getMenuDetail(1L, menuId) } returns response

        // when & then
        mockMvc.perform(get("/owner/menu/$menuId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("실내외 풀세차"))
            .andExpect(jsonPath("$.data.price").value(25000))
            .andExpect(jsonPath("$.message").value("메뉴 상세 정보를 성공적으로 조회했습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_menu_id_should_return_not_found_when_invalid_id`() {
        // given
        val menuId = 999L
        val response = ApiResponse.error<MenuResponseDto>(
            errorCode = "MENU_NOT_FOUND",
            message = "메뉴를 찾을 수 없습니다"
        )

        every { menuService.getMenuDetail(1L, menuId) } returns response

        // when & then
        mockMvc.perform(get("/owner/menu/$menuId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("MENU_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("메뉴를 찾을 수 없습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `PUT_owner_menu_id_should_update_menu_successfully`() {
        // given
        val menuId = 1L
        val request = UpdateMenuRequestDto(
            categoryName = "고급세차",
            name = "업데이트된 메뉴",
            description = "새로운 설명",
            price = BigDecimal("30000"),
            duration = 90,
            carTypes = "[\"SEDAN\", \"SUV\", \"TRUCK\"]"
        )

        val response = ApiResponse.success(
            data = MenuResponseDto(
                id = menuId,
                storeId = 1L,
                categoryName = request.categoryName!!,
                name = request.name!!,
                description = request.description,
                price = request.price!!,
                duration = request.duration!!,
                carTypes = request.carTypes,
                isActive = true,
                displayOrder = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            message = "메뉴가 성공적으로 수정되었습니다"
        )

        every { menuService.updateMenu(1L, menuId, request) } returns response

        // when & then
        mockMvc.perform(
            put("/owner/menu/$menuId")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("업데이트된 메뉴"))
            .andExpect(jsonPath("$.data.categoryName").value("고급세차"))
            .andExpect(jsonPath("$.message").value("메뉴가 성공적으로 수정되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `PUT_owner_menu_id_status_should_activate_menu_successfully`() {
        // given
        val menuId = 1L
        val response = ApiResponse.success(
            data = MenuResponseDto(
                id = menuId,
                storeId = 1L,
                categoryName = "기본세차",
                name = "테스트 메뉴",
                description = "설명",
                price = BigDecimal("25000"),
                duration = 60,
                carTypes = "[\"SEDAN\"]",
                isActive = true,
                displayOrder = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            message = "메뉴가 성공적으로 활성화되었습니다"
        )

        every { menuService.toggleMenuStatus(1L, menuId, true) } returns response

        // when & then
        mockMvc.perform(
            put("/owner/menu/$menuId/status")
                .with(csrf())
                .param("activate", "true")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.isActive").value(true))
            .andExpect(jsonPath("$.message").value("메뉴가 성공적으로 활성화되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `DELETE_owner_menu_id_should_delete_menu_successfully`() {
        // given
        val menuId = 1L
        val response = ApiResponse.success<Nothing>(
            message = "메뉴가 성공적으로 삭제되었습니다"
        )

        every { menuService.deleteMenu(1L, menuId) } returns response

        // when & then
        mockMvc.perform(
            delete("/owner/menu/$menuId")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("메뉴가 성공적으로 삭제되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `PUT_owner_menu_order_should_update_order_successfully`() {
        // given
        val request = UpdateMenuOrderRequestDto(
            menuOrders = listOf(
                MenuOrderDto(menuId = 1L, displayOrder = 2),
                MenuOrderDto(menuId = 2L, displayOrder = 1)
            )
        )

        val updatedMenus = listOf(
            MenuListResponseDto(
                id = 2L,
                categoryName = "기본세차",
                name = "메뉴2",
                price = BigDecimal("30000"),
                duration = 90,
                isActive = true,
                displayOrder = 1
            ),
            MenuListResponseDto(
                id = 1L,
                categoryName = "기본세차",
                name = "메뉴1",
                price = BigDecimal("25000"),
                duration = 60,
                isActive = true,
                displayOrder = 2
            )
        )

        val response = ApiResponse.success(
            data = updatedMenus,
            message = "메뉴 순서가 성공적으로 변경되었습니다"
        )

        every { menuService.updateMenuOrder(1L, request) } returns response

        // when & then
        mockMvc.perform(
            put("/owner/menu/order")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].displayOrder").value(1))
            .andExpect(jsonPath("$.data[1].displayOrder").value(2))
            .andExpect(jsonPath("$.message").value("메뉴 순서가 성공적으로 변경되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_menu_summary_should_return_summary_successfully`() {
        // given
        val response = ApiResponse.success(
            data = StoreMenuSummaryResponseDto(
                totalMenuCount = 10L,
                activeMenuCount = 8L,
                categories = listOf("기본세차", "고급세차"),
                recentMenus = listOf(
                    MenuListResponseDto(
                        id = 1L,
                        categoryName = "기본세차",
                        name = "최근 메뉴",
                        price = BigDecimal("25000"),
                        duration = 60,
                        isActive = true,
                        displayOrder = 1
                    )
                )
            ),
            message = "메뉴 요약 정보를 성공적으로 조회했습니다"
        )

        every { menuService.getStoreMenuSummary(1L) } returns response

        // when & then
        mockMvc.perform(get("/owner/menu/summary"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalMenuCount").value(10))
            .andExpect(jsonPath("$.data.activeMenuCount").value(8))
            .andExpect(jsonPath("$.data.categories.length()").value(2))
            .andExpect(jsonPath("$.message").value("메뉴 요약 정보를 성공적으로 조회했습니다"))
    }

    @Test
    fun `POST_owner_menu_should_return_unauthorized_when_not_authenticated`() {
        // when & then
        mockMvc.perform(
            post("/owner/menu")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "1")
    fun `POST_owner_menu_should_return_bad_request_when_invalid_data`() {
        // given
        val invalidRequest = """
        {
            "categoryName": "",
            "name": "",
            "description": "설명",
            "price": -1000,
            "duration": 0,
            "carTypes": "invalid-json",
            "displayOrder": -1
        }
        """

        // when & then
        mockMvc.perform(
            post("/owner/menu")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }
} 