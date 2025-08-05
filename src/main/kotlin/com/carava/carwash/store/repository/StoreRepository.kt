package com.carava.carwash.store.repository

import com.carava.carwash.store.entity.Store
import com.carava.carwash.store.entity.StoreCategory
import com.carava.carwash.store.entity.StoreStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StoreRepository : JpaRepository<Store, Long> {
    
    /**
     * 사장님 ID로 매장 조회
     */
    fun findByOwnerMemberId(ownerMemberId: Long): Optional<Store>
    
    /**
     * 매장 ID와 사장님 ID로 매장 조회 (소유자 확인용)
     */
    fun findByIdAndOwnerMemberId(id: Long, ownerMemberId: Long): Store?
    
    /**
     * 사장님 ID로 매장 존재 여부 확인
     */
    fun existsByOwnerMemberId(ownerMemberId: Long): Boolean
    
    /**
     * 매장명으로 중복 체크
     */
    fun existsByName(name: String): Boolean
    
    /**
     * 카테고리별 매장 조회
     */
    fun findByCategoryAndStatus(category: StoreCategory, status: StoreStatus, pageable: Pageable): Page<Store>
    
    /**
     * 상태별 매장 조회
     */
    fun findByStatus(status: StoreStatus, pageable: Pageable): Page<Store>
    
    /**
     * 매장명으로 검색
     */
    fun findByNameContainingIgnoreCaseAndStatus(name: String, status: StoreStatus, pageable: Pageable): Page<Store>
    
    /**
     * 평점 순으로 매장 조회
     */
    @Query("SELECT s FROM Store s WHERE s.status = :status ORDER BY s.averageRating DESC, s.totalReviews DESC")
    fun findByStatusOrderByRating(@Param("status") status: StoreStatus, pageable: Pageable): Page<Store>
    
    /**
     * 인기 순으로 매장 조회 (조회수 + 즐겨찾기 수)
     */
    @Query("SELECT s FROM Store s WHERE s.status = :status ORDER BY (s.viewCount + s.favoriteCount) DESC")
    fun findByStatusOrderByPopularity(@Param("status") status: StoreStatus, pageable: Pageable): Page<Store>
} 