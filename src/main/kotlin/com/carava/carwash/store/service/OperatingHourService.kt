package com.carava.carwash.store.service

import com.carava.carwash.store.dto.SaveOperatingHourRequestDto
import com.carava.carwash.store.dto.SaveOperatingHourResponseDto
import com.carava.carwash.store.entity.OperatingHour
import com.carava.carwash.store.repositoty.OperatingHourRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service("operatingHourService")
@Transactional(readOnly = true)
class OperatingHourService(
    private val operatingHourRepository: OperatingHourRepository,
    private val storeService: StoreService,
) {

    @Transactional
    fun saveOperatingHour(storeId: Long, request: SaveOperatingHourRequestDto, memberId: Long) : SaveOperatingHourResponseDto {

        val store = storeService.validateStoreOwnership(storeId, memberId)

        operatingHourRepository.deleteByStoreId(storeId)

        val operatingHours = request.operatingHours.map { dto ->
            OperatingHour(
                store = store,
                dayOfWeek = dto.dayOfWeek,
                openTime = dto.openTime,
                closeTime = dto.closeTime,
                isOpen = dto.isOpen,
            )
        }

        val savedOperatingHours = operatingHourRepository.saveAll(operatingHours)

        return SaveOperatingHourResponseDto(
            createdAt = savedOperatingHours.first().createdAt
        )
    }

}