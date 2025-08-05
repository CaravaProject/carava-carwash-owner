package com.carava.carwash.store.controller

import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.store.dto.CreateStoreRequestDto
import com.carava.carwash.store.dto.StoreResponseDto
import com.carava.carwash.store.dto.UpdateStoreRequestDto
import com.carava.carwash.store.entity.StoreCategory
import com.carava.carwash.store.service.StoreService
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

@WebMvcTest(StoreController::class)
class StoreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var storeService: StoreService

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun storeService(): StoreService = mockk()
    }

    @Test
    @WithMockUser(username = "1")
    fun `POST_owner_store_should_create_store_successfully`() {
        // given
        val request = CreateStoreRequestDto(
            name = "테스트 세차장",
            description = "깨끗한 세차 서비스",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH
        )

        val response = ApiResponse.success(
            data = StoreResponseDto(
                id = 1L,
                ownerMemberId = 1L,
                name = request.name,
                description = request.description,
                phone = request.phone,
                addressId = request.addressId,
                category = request.category,
                status = com.carava.carwash.store.entity.StoreStatus.PENDING,
                averageRating = BigDecimal.ZERO,
                totalReviews = 0,
                viewCount = 0,
                favoriteCount = 0,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            ),
            message = "매장이 성공적으로 등록되었습니다"
        )

        every { storeService.createStore(1L, request) } returns response

        // when & then
        mockMvc.perform(
            post("/owner/store")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value(request.name))
            .andExpect(jsonPath("$.message").value("매장이 성공적으로 등록되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `POST_owner_store_should_return_conflict_when_duplicate_name`() {
        // given
        val request = CreateStoreRequestDto(
            name = "중복된 세차장",
            description = "깨끗한 세차 서비스",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH
        )

        val response = ApiResponse.error<StoreResponseDto>(
            errorCode = "STORE_NAME_DUPLICATE",
            message = "이미 존재하는 매장명입니다"
        )

        every { storeService.createStore(1L, request) } returns response

        // when & then
        mockMvc.perform(
            post("/owner/store")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("STORE_NAME_DUPLICATE"))
            .andExpect(jsonPath("$.message").value("이미 존재하는 매장명입니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_store_should_return_my_store_successfully`() {
        // given
        val response = ApiResponse.success(
            data = StoreResponseDto(
                id = 1L,
                ownerMemberId = 1L,
                name = "내 세차장",
                description = "좋은 세차장",
                phone = "02-1234-5678",
                addressId = 1L,
                category = StoreCategory.CAR_WASH,
                status = com.carava.carwash.store.entity.StoreStatus.ACTIVE,
                averageRating = BigDecimal("4.5"),
                totalReviews = 10,
                viewCount = 100,
                favoriteCount = 5,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            ),
            message = "매장 정보를 성공적으로 조회했습니다"
        )

        every { storeService.getMyStore(1L) } returns response

        // when & then
        mockMvc.perform(get("/owner/store"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("내 세차장"))
            .andExpect(jsonPath("$.data.averageRating").value(4.5))
            .andExpect(jsonPath("$.message").value("매장 정보를 성공적으로 조회했습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_store_should_return_not_found_when_no_store`() {
        // given
        val response = ApiResponse.error<StoreResponseDto>(
            errorCode = "STORE_NOT_FOUND",
            message = "등록된 매장이 없습니다"
        )

        every { storeService.getMyStore(1L) } returns response

        // when & then
        mockMvc.perform(get("/owner/store"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("STORE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("등록된 매장이 없습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `PUT_owner_store_should_update_store_successfully`() {
        // given
        val request = UpdateStoreRequestDto(
            name = "업데이트된 세차장",
            description = "새로운 설명",
            phone = "02-9999-8888"
        )

        val response = ApiResponse.success(
            data = StoreResponseDto(
                id = 1L,
                ownerMemberId = 1L,
                name = request.name!!,
                description = request.description,
                phone = request.phone,
                addressId = 1L,
                category = StoreCategory.CAR_WASH,
                status = com.carava.carwash.store.entity.StoreStatus.ACTIVE,
                averageRating = BigDecimal("4.5"),
                totalReviews = 10,
                viewCount = 100,
                favoriteCount = 5,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            ),
            message = "매장 정보가 성공적으로 수정되었습니다"
        )

        every { storeService.updateStore(1L, request) } returns response

        // when & then
        mockMvc.perform(
            put("/owner/store")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("업데이트된 세차장"))
            .andExpect(jsonPath("$.message").value("매장 정보가 성공적으로 수정되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `PUT_owner_store_status_should_activate_store_successfully`() {
        // given
        val response = ApiResponse.success(
            data = StoreResponseDto(
                id = 1L,
                ownerMemberId = 1L,
                name = "테스트 세차장",
                description = "설명",
                phone = "02-1234-5678",
                addressId = 1L,
                category = StoreCategory.CAR_WASH,
                status = com.carava.carwash.store.entity.StoreStatus.ACTIVE,
                averageRating = BigDecimal.ZERO,
                totalReviews = 0,
                viewCount = 0,
                favoriteCount = 0,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            ),
            message = "매장이 성공적으로 활성화되었습니다"
        )

        every { storeService.toggleStoreStatus(1L, true) } returns response

        // when & then
        mockMvc.perform(
            put("/owner/store/status")
                .with(csrf())
                .param("activate", "true")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"))
            .andExpect(jsonPath("$.message").value("매장이 성공적으로 활성화되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `DELETE_owner_store_should_delete_store_successfully`() {
        // given
        val response = ApiResponse.success<Nothing>(
            message = "매장이 성공적으로 삭제되었습니다"
        )

        every { storeService.deleteStore(1L) } returns response

        // when & then
        mockMvc.perform(
            delete("/owner/store")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("매장이 성공적으로 삭제되었습니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_store_check_name_should_return_available_name`() {
        // given
        val storeName = "새로운 세차장"
        val response = ApiResponse.success(
            data = true,
            message = "사용 가능한 매장명입니다"
        )

        every { storeService.checkStoreName(storeName) } returns response

        // when & then
        mockMvc.perform(
            get("/owner/store/check-name")
                .param("name", storeName)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(true))
            .andExpect(jsonPath("$.message").value("사용 가능한 매장명입니다"))
    }

    @Test
    @WithMockUser(username = "1")
    fun `GET_owner_store_check_name_should_return_unavailable_name`() {
        // given
        val storeName = "중복된 세차장"
        val response = ApiResponse.success(
            data = false,
            message = "이미 존재하는 매장명입니다"
        )

        every { storeService.checkStoreName(storeName) } returns response

        // when & then
        mockMvc.perform(
            get("/owner/store/check-name")
                .param("name", storeName)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(false))
            .andExpect(jsonPath("$.message").value("이미 존재하는 매장명입니다"))
    }

    @Test
    fun `POST_owner_store_should_return_unauthorized_when_not_authenticated`() {
        // when & then
        mockMvc.perform(
            post("/owner/store")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "1")
    fun `POST_owner_store_should_return_bad_request_when_invalid_data`() {
        // given
        val invalidRequest = """
        {
            "name": "",
            "description": "설명",
            "phone": "invalid-phone",
            "addressId": null,
            "category": "INVALID_CATEGORY"
        }
        """

        // when & then
        mockMvc.perform(
            post("/owner/store")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }
} 