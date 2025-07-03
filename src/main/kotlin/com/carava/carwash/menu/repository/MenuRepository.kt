package com.carava.carwash.menu.repository

import com.carava.carwash.menu.entity.Menu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository("menuRepository")
interface MenuRepository : JpaRepository<Menu, Long> {
}