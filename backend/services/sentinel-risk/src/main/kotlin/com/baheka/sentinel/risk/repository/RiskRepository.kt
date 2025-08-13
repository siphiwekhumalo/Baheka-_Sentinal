package com.baheka.sentinel.risk.repository

import com.baheka.sentinel.risk.entity.RiskProfile
import com.baheka.sentinel.risk.entity.RiskMetric
import com.baheka.sentinel.risk.entity.RiskCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface RiskProfileRepository : JpaRepository<RiskProfile, UUID> {
    
    fun findByOrganizationIdAndCustomerId(organizationId: UUID, customerId: String): RiskProfile?
    
    fun findByOrganizationId(organizationId: UUID): List<RiskProfile>
    
    fun findByOrganizationIdAndRiskCategory(organizationId: UUID, riskCategory: RiskCategory): List<RiskProfile>
    
    @Query("""
        SELECT rp FROM RiskProfile rp 
        WHERE rp.organizationId = :organizationId 
        AND rp.riskScore >= :minScore 
        ORDER BY rp.riskScore DESC
    """)
    fun findHighRiskProfiles(
        @Param("organizationId") organizationId: UUID,
        @Param("minScore") minScore: java.math.BigDecimal
    ): List<RiskProfile>
    
    @Query("""
        SELECT COUNT(rp) FROM RiskProfile rp 
        WHERE rp.organizationId = :organizationId 
        AND rp.riskCategory = :category
    """)
    fun countByRiskCategory(
        @Param("organizationId") organizationId: UUID,
        @Param("category") category: RiskCategory
    ): Long
}

@Repository
interface RiskMetricRepository : JpaRepository<RiskMetric, UUID> {
    
    fun findByOrganizationIdAndMetricTypeOrderByTimeDesc(
        organizationId: UUID, 
        metricType: String
    ): List<RiskMetric>
    
    @Query("""
        SELECT rm FROM RiskMetric rm 
        WHERE rm.organizationId = :organizationId 
        AND rm.metricType = :metricType 
        AND rm.time BETWEEN :startTime AND :endTime
        ORDER BY rm.time ASC
    """)
    fun findMetricsBetween(
        @Param("organizationId") organizationId: UUID,
        @Param("metricType") metricType: String,
        @Param("startTime") startTime: Instant,
        @Param("endTime") endTime: Instant
    ): List<RiskMetric>
    
    @Query("""
        SELECT rm FROM RiskMetric rm 
        WHERE rm.organizationId = :organizationId 
        AND rm.metricType = :metricType 
        ORDER BY rm.time DESC 
        LIMIT 1
    """)
    fun findLatestMetric(
        @Param("organizationId") organizationId: UUID,
        @Param("metricType") metricType: String
    ): RiskMetric?
}
