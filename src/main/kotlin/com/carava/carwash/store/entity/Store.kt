package com.carava.carwash.store.entity

import com.carava.carwash.global.entity.BaseEntity
import jakarta.persistence.*

@Entity(name = "store")
@Table(name = "store")
class Store (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var ownerMemberId: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: StoreCategory = StoreCategory.CAR_WASH,
) : BaseEntity()