package com.carava.carwash.store.repositoty

import com.carava.carwash.store.entity.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository("storeRepository")
interface StoreRepository : JpaRepository<Store, Long> {
    override fun findById(id: Long): Optional<Store>
}