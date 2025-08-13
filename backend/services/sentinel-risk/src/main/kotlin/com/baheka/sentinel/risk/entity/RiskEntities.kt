package com.baheka.sentinel.risk.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "risk_profiles", schema = "sentinel_risk")
data class RiskProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    
    @Column(name = "organization_id", nullable = false)
    val organizationId: UUID,
    
    @Column(name = "customer_id", nullable = false)
    val customerId: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false)
    val profileType: ProfileType,
    
    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    val riskScore: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false)
    val riskCategory: RiskCategory,
    
    @Column(name = "pd_score", precision = 8, scale = 6)
    val pdScore: BigDecimal? = null, // Probability of Default
    
    @Column(name = "lgd_score", precision = 8, scale = 6)
    val lgdScore: BigDecimal? = null, // Loss Given Default
    
    @Column(name = "ead_amount", precision = 18, scale = 2)
    val eadAmount: BigDecimal? = null, // Exposure at Default
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
)

enum class ProfileType {
    INDIVIDUAL, CORPORATE
}

enum class RiskCategory {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Entity
@Table(name = "risk_metrics", schema = "sentinel_risk")
data class RiskMetric(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    
    @Column(name = "time", nullable = false)
    val time: Instant,
    
    @Column(name = "organization_id", nullable = false)
    val organizationId: UUID,
    
    @Column(name = "metric_type", nullable = false)
    val metricType: String, // VaR, CVaR, CAPITAL_RATIO
    
    @Column(name = "metric_value", nullable = false, precision = 18, scale = 6)
    val metricValue: BigDecimal,
    
    @Column(name = "currency", length = 3)
    val currency: String = "USD",
    
    @Column(name = "confidence_level", precision = 5, scale = 2)
    val confidenceLevel: BigDecimal? = null,
    
    @Column(name = "time_horizon")
    val timeHorizon: Int? = null, // days
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
