package com.carava.carwash.store.service

import com.carava.carwash.global.exception.ForbiddenException
import com.carava.carwash.global.exception.NotFoundException
import com.carava.carwash.store.dto.CreateStoreRequestDto
import com.carava.carwash.store.dto.CreateStoreResponseDto
import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.repositoty.StoreRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("storeService")
@Transactional(readOnly = true)
class StoreService(
    private val storeRepository: StoreRepository
) {

    @Transactional
    fun createStore(request: CreateStoreRequestDto, ownerMemberId: Long): CreateStoreResponseDto {

        val store = Store(
            name = request.name,
            ownerMemberId = ownerMemberId,
            category = request.category,
        )
        val savedStore = storeRepository.save(store)

        return CreateStoreResponseDto(
            storeId = savedStore.id,
            createdAt = savedStore.createdAt
        )
    }

    fun validateStoreOwnership(storeId: Long, ownerMemberId: Long) {
        val store = storeRepository.findById(storeId)
            .orElseThrow{ NotFoundException("가게를 찾을 수 없습니다.") }

        if (store.ownerMemberId != ownerMemberId) {
            throw ForbiddenException("해당 가게에 대한 권한이 없습니다.")
        }

    }

}