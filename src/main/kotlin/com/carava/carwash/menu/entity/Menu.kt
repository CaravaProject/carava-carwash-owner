package com.carava.carwash.menu.entity

import com.carava.carwash.global.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity(name = "menu")
@Table(name = "menu")
class Menu (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var storeId: Long = 0,

    @Column(nullable = false)
    var price: BigDecimal,

) : BaseEntity()