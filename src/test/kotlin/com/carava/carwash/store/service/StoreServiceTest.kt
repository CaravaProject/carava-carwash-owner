package com.carava.carwash.store.service

import com.carava.carwash.store.dto.CreateStoreRequestDto
import com.carava.carwash.store.dto.UpdateStoreRequestDto
import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.entity.StoreCategory
import com.carava.carwash.store.entity.StoreStatus
import com.carava.carwash.store.repository.StoreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StoreServiceTest {

    private val storeRepository = mockk<StoreRepository>()
    
    private val storeService = StoreService(
        storeRepository = storeRepository
    )

    @Test
    fun `given_valid_request_when_create_store_then_success`() {
        // given
        val ownerId = 1L
        val request = CreateStoreRequestDto(
            name = "테스트 세차장",
            description = "깨끗한 세차 서비스",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH
        )

        val savedStore = Store(
            id = 1L,
            ownerMemberId = ownerId,
            name = request.name,
            description = request.description,
            phone = request.phone,
            addressId = request.addressId,
            category = request.category,
            status = StoreStatus.PENDING
        )

        every { storeRepository.existsByOwnerMemberId(ownerId) } returns false
        every { storeRepository.existsByName(request.name) } returns false
        every { storeRepository.save(any()) } returns savedStore

        // when
        val response = storeService.createStore(ownerId, request)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("매장이 성공적으로 등록되었습니다", response.message)
        assertEquals(request.name, response.data?.name)
        assertEquals(StoreStatus.PENDING, response.data?.status)

        verify { storeRepository.save(any()) }
    }

    @Test
    fun `given_duplicate_owner_when_create_store_then_fail`() {
        // given
        val ownerId = 1L
        val request = CreateStoreRequestDto(
            name = "테스트 세차장",
            description = "깨끗한 세차 서비스",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH
        )

        every { storeRepository.existsByOwnerMemberId(ownerId) } returns true

        // when
        val response = storeService.createStore(ownerId, request)

        // then
        assertFalse(response.success)
        assertEquals("STORE_ALREADY_EXISTS", response.errorCode)
        assertEquals("이미 등록된 매장이 있습니다", response.message)
    }

    @Test
    fun `given_duplicate_name_when_create_store_then_fail`() {
        // given
        val ownerId = 1L
        val request = CreateStoreRequestDto(
            name = "중복된 세차장",
            description = "깨끗한 세차 서비스",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH
        )

        every { storeRepository.existsByOwnerMemberId(ownerId) } returns false
        every { storeRepository.existsByName(request.name) } returns true

        // when
        val response = storeService.createStore(ownerId, request)

        // then
        assertFalse(response.success)
        assertEquals("STORE_NAME_DUPLICATE", response.errorCode)
        assertEquals("이미 존재하는 매장명입니다", response.message)
    }

    @Test
    fun `given_valid_owner_when_get_my_store_then_success`() {
        // given
        val ownerId = 1L
        val store = Store(
            id = 1L,
            ownerMemberId = ownerId,
            name = "내 세차장",
            description = "좋은 세차장",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH,
            status = StoreStatus.ACTIVE
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(store)

        // when
        val response = storeService.getMyStore(ownerId)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("매장 정보를 성공적으로 조회했습니다", response.message)
        assertEquals(store.name, response.data?.name)
        assertEquals(store.status, response.data?.status)
    }

    @Test
    fun `given_no_store_owner_when_get_my_store_then_fail`() {
        // given
        val ownerId = 1L

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.empty()

        // when
        val response = storeService.getMyStore(ownerId)

        // then
        assertFalse(response.success)
        assertEquals("STORE_NOT_FOUND", response.errorCode)
        assertEquals("등록된 매장이 없습니다", response.message)
    }

    @Test
    fun `given_valid_update_request_when_update_store_then_success`() {
        // given
        val ownerId = 1L
        val updateRequest = UpdateStoreRequestDto(
            name = "업데이트된 세차장",
            description = "새로운 설명",
            phone = "02-9999-8888"
        )

        val existingStore = Store(
            id = 1L,
            ownerMemberId = ownerId,
            name = "기존 세차장",
            description = "기존 설명",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH,
            status = StoreStatus.ACTIVE
        )

        val updatedStore = existingStore.copy().apply {
            updateBasicInfo(
                name = updateRequest.name,
                description = updateRequest.description,
                phone = updateRequest.phone
            )
        }

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(existingStore)
        every { storeRepository.existsByName(updateRequest.name!!) } returns false
        every { storeRepository.save(any()) } returns updatedStore

        // when
        val response = storeService.updateStore(ownerId, updateRequest)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("매장 정보가 성공적으로 수정되었습니다", response.message)

        verify { storeRepository.save(any()) }
    }

    @Test
    fun `given_activate_request_when_toggle_store_status_then_success`() {
        // given
        val ownerId = 1L
        val activate = true

        val store = Store(
            id = 1L,
            ownerMemberId = ownerId,
            name = "테스트 세차장",
            description = "설명",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH,
            status = StoreStatus.INACTIVE
        )

        val activatedStore = store.copy().apply { activate() }

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(store)
        every { storeRepository.save(any()) } returns activatedStore

        // when
        val response = storeService.toggleStoreStatus(ownerId, activate)

        // then
        assertTrue(response.success)
        assertNotNull(response.data)
        assertEquals("매장이 성공적으로 활성화되었습니다", response.message)

        verify { storeRepository.save(any()) }
    }

    @Test
    fun `given_valid_store_name_when_check_store_name_then_available`() {
        // given
        val storeName = "새로운 세차장"

        every { storeRepository.existsByName(storeName) } returns false

        // when
        val response = storeService.checkStoreName(storeName)

        // then
        assertTrue(response.success)
        assertTrue(response.data == true)
        assertEquals("사용 가능한 매장명입니다", response.message)
    }

    @Test
    fun `given_duplicate_store_name_when_check_store_name_then_unavailable`() {
        // given
        val storeName = "중복된 세차장"

        every { storeRepository.existsByName(storeName) } returns true

        // when
        val response = storeService.checkStoreName(storeName)

        // then
        assertTrue(response.success)
        assertFalse(response.data == true)
        assertEquals("이미 존재하는 매장명입니다", response.message)
    }

    @Test
    fun `given_valid_owner_when_delete_store_then_success`() {
        // given
        val ownerId = 1L

        val store = Store(
            id = 1L,
            ownerMemberId = ownerId,
            name = "삭제할 세차장",
            description = "설명",
            phone = "02-1234-5678",
            addressId = 1L,
            category = StoreCategory.CAR_WASH,
            status = StoreStatus.ACTIVE
        )

        every { storeRepository.findByOwnerMemberId(ownerId) } returns Optional.of(store)
        every { storeRepository.save(any()) } returns store.apply { deactivate() }

        // when
        val response = storeService.deleteStore(ownerId)

        // then
        assertTrue(response.success)
        assertEquals("매장이 성공적으로 삭제되었습니다", response.message)

        verify { storeRepository.save(any()) }
    }
} 