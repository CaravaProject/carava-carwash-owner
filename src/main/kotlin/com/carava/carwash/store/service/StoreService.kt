package com.carava.carwash.store.service

import com.carava.carwash.global.dto.ApiResponse
import com.carava.carwash.store.dto.*
import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.entity.StoreStatus
import com.carava.carwash.store.repository.StoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StoreService(
    private val storeRepository: StoreRepository
) {

    /**
     * 매장 등록
     */
    @Transactional
    fun createStore(ownerId: Long, request: CreateStoreRequestDto): ApiResponse<StoreResponseDto> {
        return try {
            // 사장님이 이미 매장을 가지고 있는지 확인
            if (storeRepository.existsByOwnerMemberId(ownerId)) {
                return ApiResponse.error(
                    errorCode = "STORE_ALREADY_EXISTS",
                    message = "이미 등록된 매장이 있습니다"
                )
            }

            // 매장명 중복 확인
            if (storeRepository.existsByName(request.name)) {
                return ApiResponse.error(
                    errorCode = "STORE_NAME_DUPLICATE",
                    message = "이미 존재하는 매장명입니다"
                )
            }

            // 매장 생성
            val store = Store(
                ownerMemberId = ownerId,
                name = request.name,
                description = request.description,
                phone = request.phone,
                addressId = request.addressId,
                category = request.category,
                status = StoreStatus.PENDING
            )

            val savedStore = storeRepository.save(store)

            ApiResponse.success(
                data = StoreResponseDto.from(savedStore),
                message = "매장이 성공적으로 등록되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "STORE_CREATE_FAILED",
                message = "매장 등록 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 내 매장 정보 조회
     */
    fun getMyStore(ownerId: Long): ApiResponse<StoreResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "등록된 매장이 없습니다"
                )

            ApiResponse.success(
                data = StoreResponseDto.from(store),
                message = "매장 정보를 성공적으로 조회했습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "STORE_GET_FAILED",
                message = "매장 정보 조회 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 매장 정보 수정
     */
    @Transactional
    fun updateStore(ownerId: Long, request: UpdateStoreRequestDto): ApiResponse<StoreResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "수정할 매장이 없습니다"
                )

            // 매장명 중복 확인 (다른 매장과의 중복)
            if (request.name != null && request.name != store.name) {
                if (storeRepository.existsByName(request.name)) {
                    return ApiResponse.error(
                        errorCode = "STORE_NAME_DUPLICATE",
                        message = "이미 존재하는 매장명입니다"
                    )
                }
            }

            // 매장 정보 업데이트
            store.updateBasicInfo(
                name = request.name,
                description = request.description,
                phone = request.phone
            )

            val updatedStore = storeRepository.save(store)

            ApiResponse.success(
                data = StoreResponseDto.from(updatedStore),
                message = "매장 정보가 성공적으로 수정되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "STORE_UPDATE_FAILED",
                message = "매장 정보 수정 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 매장 활성화/비활성화
     */
    @Transactional
    fun toggleStoreStatus(ownerId: Long, activate: Boolean): ApiResponse<StoreResponseDto> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "변경할 매장이 없습니다"
                )

            // 상태 변경
            if (activate) {
                store.activate()
            } else {
                store.deactivate()
            }

            val updatedStore = storeRepository.save(store)

            val statusMessage = if (activate) "활성화" else "비활성화"
            ApiResponse.success(
                data = StoreResponseDto.from(updatedStore),
                message = "매장이 성공적으로 ${statusMessage}되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "STORE_STATUS_UPDATE_FAILED",
                message = "매장 상태 변경 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 매장 삭제 (실제로는 상태를 INACTIVE로 변경)
     */
    @Transactional
    fun deleteStore(ownerId: Long): ApiResponse<Nothing> {
        return try {
            val store = storeRepository.findByOwnerMemberId(ownerId)
                .orElse(null)
                ?: return ApiResponse.error(
                    errorCode = "STORE_NOT_FOUND",
                    message = "삭제할 매장이 없습니다"
                )

            store.deactivate()
            storeRepository.save(store)

            ApiResponse.success<Nothing>(
                message = "매장이 성공적으로 삭제되었습니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "STORE_DELETE_FAILED",
                message = "매장 삭제 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    /**
     * 매장명 중복 확인
     */
    fun checkStoreName(name: String): ApiResponse<Boolean> {
        return try {
            val isAvailable = !storeRepository.existsByName(name)
            
            ApiResponse.success(
                data = isAvailable,
                message = if (isAvailable) "사용 가능한 매장명입니다" else "이미 존재하는 매장명입니다"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                errorCode = "STORE_NAME_CHECK_FAILED",
                message = "매장명 확인 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }
} 