package com.carava.carwash.menu.service

import com.carava.carwash.menu.dto.CreateMenuRequestDto
import com.carava.carwash.menu.dto.CreateMenuResponseDto
import com.carava.carwash.menu.entity.Menu
import com.carava.carwash.menu.repository.MenuRepository
import com.carava.carwash.store.service.StoreService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("menuService")
@Transactional(readOnly = true)
class MenuService(
    private val menuRepository: MenuRepository,
    private val storeService: StoreService,
) {

    @Transactional
    fun createMenu(request: CreateMenuRequestDto, memberId: Long) : CreateMenuResponseDto {

        storeService.validateStoreOwnership(request.storeId, memberId)

        val menu = Menu(
            storeId = request.storeId,
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