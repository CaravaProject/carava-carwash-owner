package com.carava.carwash.store.entity

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    var store: Store,

    @Column(nullable = false)
    var price: BigDecimal,

) : BaseEntity()