package com.carava.carwash.store.service

import com.carava.carwash.store.dto.CreateStoreRequestDto
import com.carava.carwash.store.dto.CreateStoreResponseDto
import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.repositoty.StoreRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service("storeService")
@Transactional
class StoreService(
    private val storeRepository: StoreRepository
) {

    fun createStore(request: CreateStoreRequestDto, ownerMemberId: Long): CreateStoreResponseDto {

        val store = Store(
            name = request.name,
            ownerMemberId = ownerMemberId,
            category = request.category,
        )
        val savedStore = storeRepository.save(store)

        return CreateStoreResponseDto(
            storeId = savedStore.id,
            createdAt = savedStore.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

}