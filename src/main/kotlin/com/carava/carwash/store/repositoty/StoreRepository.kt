package com.carava.carwash.store.repositoty

import com.carava.carwash.store.entity.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository("storeRepository")
interface StoreRepository : JpaRepository<Store, Long> {

}