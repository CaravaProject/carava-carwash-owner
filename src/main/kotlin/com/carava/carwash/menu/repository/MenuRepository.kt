package com.carava.carwash.menu.repository

import com.carava.carwash.menu.entity.Menu
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MenuRepository : JpaRepository<Menu, Long> {
    
    /**
     * 매장별 메뉴 조회
     */
    fun findByStoreId(storeId: Long): List<Menu>
    
    /**
     * 매장별 활성 메뉴 조회
     */
    fun findByStoreIdAndIsActiveTrue(storeId: Long): List<Menu>
    
    /**
     * 매장별 메뉴 페이징 조회
     */
    fun findByStoreId(storeId: Long, pageable: Pageable): Page<Menu>
    
    /**
     * 매장별 활성 메뉴 페이징 조회
     */
    fun findByStoreIdAndIsActiveTrue(storeId: Long, pageable: Pageable): Page<Menu>
    
    /**
     * 매장별 카테고리별 메뉴 조회
     */
    fun findByStoreIdAndCategoryName(storeId: Long, categoryName: String): List<Menu>
    
    /**
     * 매장별 카테고리별 활성 메뉴 조회
     */
    fun findByStoreIdAndCategoryNameAndIsActiveTrue(storeId: Long, categoryName: String): List<Menu>
    
    /**
     * 매장별 메뉴명으로 검색
     */
    fun findByStoreIdAndNameContainingIgnoreCase(storeId: Long, name: String): List<Menu>
    
    /**
     * 매장별 특정 메뉴 ID와 매장 ID로 조회 (권한 확인용)
     */
    fun findByIdAndStoreId(id: Long, storeId: Long): Optional<Menu>
    
    /**
     * 매장별 메뉴 존재 여부 확인
     */
    fun existsByStoreIdAndName(storeId: Long, name: String): Boolean
    
    /**
     * 매장별 메뉴를 표시 순서대로 조회
     */
    @Query("SELECT m FROM Menu m WHERE m.storeId = :storeId ORDER BY m.displayOrder ASC, m.createdAt ASC")
    fun findByStoreIdOrderByDisplayOrder(@Param("storeId") storeId: Long): List<Menu>
    
    /**
     * 매장별 활성 메뉴를 표시 순서대로 조회
     */
    @Query("SELECT m FROM Menu m WHERE m.storeId = :storeId AND m.isActive = true ORDER BY m.displayOrder ASC, m.createdAt ASC")
    fun findByStoreIdAndIsActiveTrueOrderByDisplayOrder(@Param("storeId") storeId: Long): List<Menu>
    
    /**
     * 매장별 카테고리 목록 조회
     */
    @Query("SELECT DISTINCT m.categoryName FROM Menu m WHERE m.storeId = :storeId ORDER BY m.categoryName")
    fun findDistinctCategoryNamesByStoreId(@Param("storeId") storeId: Long): List<String>
    
    /**
     * 매장별 활성 메뉴 개수 조회
     */
    fun countByStoreIdAndIsActiveTrue(storeId: Long): Long
    
    /**
     * 매장 삭제 시 관련 메뉴 삭제
     */
    fun deleteByStoreId(storeId: Long)
} 