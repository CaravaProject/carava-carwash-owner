package com.carava.carwash.store.repositoty

import com.carava.carwash.store.entity.Menu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository("menuRepository")
interface MenuRepository : JpaRepository<Menu, Long> {
}