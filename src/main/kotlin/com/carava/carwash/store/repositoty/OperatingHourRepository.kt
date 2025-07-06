package com.carava.carwash.store.repositoty

import com.carava.carwash.store.entity.OperatingHour
import org.springframework.data.jpa.repository.JpaRepository

interface OperatingHourRepository : JpaRepository<OperatingHour, Long>{

    fun deleteByStoreId(storeId: Long)
}