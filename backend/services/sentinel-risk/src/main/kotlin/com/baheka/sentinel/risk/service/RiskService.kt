package com.baheka.sentinel.risk.service

import com.baheka.sentinel.risk.entity.RiskProfile
import com.baheka.sentinel.risk.entity.RiskMetric
import com.baheka.sentinel.risk.entity.RiskCategory
import com.baheka.sentinel.risk.entity.ProfileType
import com.baheka.sentinel.risk.repository.RiskProfileRepository
import com.baheka.sentinel.risk.repository.RiskMetricRepository
import com.baheka.sentinel.common.exception.ResourceNotFoundException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional
class RiskService(
    private val riskProfileRepository: RiskProfileRepository,
    private val riskMetricRepository: RiskMetricRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val riskCalculationService: RiskCalculationService
) {
    
    fun calculateRiskProfile(organizationId: UUID, customerId: String, profileType: ProfileType): RiskProfile {
        // Calculate risk score using ML model
        val riskScore = riskCalculationService.calculateRiskScore(organizationId, customerId, profileType)
        val riskCategory = determineRiskCategory(riskScore)
        
        // Calculate Basel III metrics if corporate
        val pdScore = if (profileType == ProfileType.CORPORATE) {
            riskCalculationService.calculatePD(organizationId, customerId)
        } else null
        
        val lgdScore = if (profileType == ProfileType.CORPORATE) {
            riskCalculationService.calculateLGD(organizationId, customerId)
        } else null
        
        val eadAmount = if (profileType == ProfileType.CORPORATE) {
            riskCalculationService.calculateEAD(organizationId, customerId)
        } else null
        
        // Check if profile exists and update or create new
        val existingProfile = riskProfileRepository.findByOrganizationIdAndCustomerId(organizationId, customerId)
        
        val riskProfile = if (existingProfile != null) {
            existingProfile.copy(
                riskScore = riskScore,
                riskCategory = riskCategory,
                pdScore = pdScore,
                lgdScore = lgdScore,
                eadAmount = eadAmount,
                updatedAt = Instant.now()
            )
        } else {
            RiskProfile(
                organizationId = organizationId,
                customerId = customerId,
                profileType = profileType,
                riskScore = riskScore,
                riskCategory = riskCategory,
                pdScore = pdScore,
                lgdScore = lgdScore,
                eadAmount = eadAmount
            )
        }
        
        val savedProfile = riskProfileRepository.save(riskProfile)
        
        // Publish risk event to Kafka
        kafkaTemplate.send("risk-events", savedProfile)
        
        return savedProfile
    }
    
    fun getRiskProfile(organizationId: UUID, customerId: String): RiskProfile {
        return riskProfileRepository.findByOrganizationIdAndCustomerId(organizationId, customerId)
            ?: throw ResourceNotFoundException("RiskProfile", "customerId", customerId)
    }
    
    fun getAllRiskProfiles(organizationId: UUID): List<RiskProfile> {
        return riskProfileRepository.findByOrganizationId(organizationId)
    }
    
    fun getHighRiskProfiles(organizationId: UUID, threshold: BigDecimal = BigDecimal("70.0")): List<RiskProfile> {
        return riskProfileRepository.findHighRiskProfiles(organizationId, threshold)
    }
    
    fun calculateVaR(organizationId: UUID, confidenceLevel: BigDecimal, timeHorizon: Int): RiskMetric {
        val varValue = riskCalculationService.calculateVaR(organizationId, confidenceLevel, timeHorizon)
        
        val riskMetric = RiskMetric(
            time = Instant.now(),
            organizationId = organizationId,
            metricType = "VaR",
            metricValue = varValue,
            confidenceLevel = confidenceLevel,
            timeHorizon = timeHorizon
        )
        
        val savedMetric = riskMetricRepository.save(riskMetric)
        
        // Publish metric to Kafka
        kafkaTemplate.send("risk-events", savedMetric)
        
        return savedMetric
    }
    
    fun getCapitalRatio(organizationId: UUID): RiskMetric? {
        return riskMetricRepository.findLatestMetric(organizationId, "CAPITAL_RATIO")
    }
    
    fun getRiskMetrics(organizationId: UUID, metricType: String, days: Long = 30): List<RiskMetric> {
        val endTime = Instant.now()
        val startTime = endTime.minus(days, ChronoUnit.DAYS)
        return riskMetricRepository.findMetricsBetween(organizationId, metricType, startTime, endTime)
    }
    
    fun getRiskSummary(organizationId: UUID): Map<String, Any> {
        val lowRiskCount = riskProfileRepository.countByRiskCategory(organizationId, RiskCategory.LOW)
        val mediumRiskCount = riskProfileRepository.countByRiskCategory(organizationId, RiskCategory.MEDIUM)
        val highRiskCount = riskProfileRepository.countByRiskCategory(organizationId, RiskCategory.HIGH)
        val criticalRiskCount = riskProfileRepository.countByRiskCategory(organizationId, RiskCategory.CRITICAL)
        val totalProfiles = lowRiskCount + mediumRiskCount + highRiskCount + criticalRiskCount
        
        val latestVaR = riskMetricRepository.findLatestMetric(organizationId, "VaR")
        val latestCapitalRatio = riskMetricRepository.findLatestMetric(organizationId, "CAPITAL_RATIO")
        
        return mapOf(
            "totalProfiles" to totalProfiles,
            "riskDistribution" to mapOf(
                "low" to lowRiskCount,
                "medium" to mediumRiskCount,
                "high" to highRiskCount,
                "critical" to criticalRiskCount
            ),
            "latestVaR" to latestVaR?.metricValue,
            "latestCapitalRatio" to latestCapitalRatio?.metricValue,
            "lastUpdated" to Instant.now()
        )
    }
    
    private fun determineRiskCategory(riskScore: BigDecimal): RiskCategory {
        return when {
            riskScore < BigDecimal("25.0") -> RiskCategory.LOW
            riskScore < BigDecimal("50.0") -> RiskCategory.MEDIUM
            riskScore < BigDecimal("75.0") -> RiskCategory.HIGH
            else -> RiskCategory.CRITICAL
        }
    }
}
