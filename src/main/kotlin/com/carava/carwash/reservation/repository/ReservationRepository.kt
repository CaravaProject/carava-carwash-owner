package com.carava.carwash.reservation.repository

import com.carava.carwash.reservation.entity.Reservation
import com.carava.carwash.reservation.entity.ReservationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {
    
    // 매장별 예약 조회
    fun findByStoreId(storeId: Long, pageable: Pageable): Page<Reservation>
    
    // 매장별 + 상태별 예약 조회
    fun findByStoreIdAndStatus(storeId: Long, status: ReservationStatus, pageable: Pageable): Page<Reservation>
    
    // 매장별 + 날짜별 예약 조회
    fun findByStoreIdAndReservationDate(storeId: Long, reservationDate: LocalDate): List<Reservation>
    
    // 매장별 + 날짜 범위별 예약 조회
    fun findByStoreIdAndReservationDateBetween(
        storeId: Long, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<Reservation>
    
    // 매장별 대기 중인 예약 개수
    fun countByStoreIdAndStatus(storeId: Long, status: ReservationStatus): Long
    
    // 매장별 예약 현황 요약
    @Query("""
        SELECT r.status, COUNT(r) 
        FROM Reservation r 
        WHERE r.storeId = :storeId 
        AND r.reservationDate BETWEEN :startDate AND :endDate
        GROUP BY r.status
    """)
    fun getReservationSummaryByStore(
        @Param("storeId") storeId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>
} 