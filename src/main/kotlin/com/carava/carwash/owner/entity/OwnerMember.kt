package com.carava.carwash.owner.entity

import com.carava.carwash.shared.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "owner_member",
    indexes = [
        Index(name = "idx_owner_member_auth_id", columnList = "auth_id", unique = true),
        Index(name = "idx_owner_member_business_number", columnList = "business_number")
    ]
)
class OwnerMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "auth_id", nullable = false)
    val authId: Long,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var phone: String,

    @Column(name = "address_id")
    var addressId: Long? = null,

    @Column(name = "business_number", nullable = false, unique = true)
    val businessNumber: String,

    @Column(name = "business_name", nullable = false)
    var businessName: String
) : BaseEntity() {

    fun updateProfile(name: String, phone: String, businessName: String) {
        this.name = name
        this.phone = phone
        this.businessName = businessName
    }

    fun updateAddress(addressId: Long) {
        this.addressId = addressId
    }
} 