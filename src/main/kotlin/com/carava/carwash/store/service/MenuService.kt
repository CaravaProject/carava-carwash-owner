package com.carava.carwash.store.service

import com.carava.carwash.store.dto.CreateMenuRequestDto
import com.carava.carwash.store.dto.CreateMenuResponseDto
import com.carava.carwash.store.entity.Menu
import com.carava.carwash.store.repositoty.MenuRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("menuService")
@Transactional(readOnly = true)
class MenuService(
    private val menuRepository: MenuRepository,
    private val storeService: StoreService,
) {

    @Transactional
    fun createMenu(storeId: Long, request: CreateMenuRequestDto, memberId: Long) : CreateMenuResponseDto {

        val store = storeService.validateStoreOwnership(storeId, memberId)

        val menu = Menu(
            store = store,
            name = request.name,
            price = request.price,
        )
        val savedMenu = menuRepository.save(menu)

        return CreateMenuResponseDto(
            menuId = savedMenu.id,
            name = savedMenu.name,
            createdAt = savedMenu.createdAt
        )

    }

}