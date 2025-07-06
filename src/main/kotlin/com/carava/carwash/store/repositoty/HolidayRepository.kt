package com.carava.carwash.store.repositoty

import com.carava.carwash.store.entity.Holiday
import org.springframework.data.jpa.repository.JpaRepository

interface HolidayRepository : JpaRepository<Holiday, Long> {
}