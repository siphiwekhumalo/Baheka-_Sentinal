package com.baheka.sentinel.risk.controller

import com.baheka.sentinel.risk.entity.RiskProfile
import com.baheka.sentinel.risk.entity.RiskMetric
import com.baheka.sentinel.risk.entity.ProfileType
import com.baheka.sentinel.risk.service.RiskService
import com.baheka.sentinel.common.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/api/v1/risk")
class RiskController(
    private val riskService: RiskService
) {
    
    @PostMapping("/profiles")
    fun calculateRiskProfile(
        @RequestParam organizationId: UUID,
        @RequestParam customerId: String,
        @RequestParam profileType: ProfileType
    ): ResponseEntity<ApiResponse<RiskProfile>> {
        val riskProfile = riskService.calculateRiskProfile(organizationId, customerId, profileType)
        return ResponseEntity.ok(ApiResponse.success(riskProfile, "Risk profile calculated successfully"))
    }
    
    @GetMapping("/profiles/{customerId}")
    fun getRiskProfile(
        @PathVariable customerId: String,
        @RequestParam organizationId: UUID
    ): ResponseEntity<ApiResponse<RiskProfile>> {
        val riskProfile = riskService.getRiskProfile(organizationId, customerId)
        return ResponseEntity.ok(ApiResponse.success(riskProfile))
    }
    
    @GetMapping("/profiles")
    fun getAllRiskProfiles(
        @RequestParam organizationId: UUID
    ): ResponseEntity<ApiResponse<List<RiskProfile>>> {
        val profiles = riskService.getAllRiskProfiles(organizationId)
        return ResponseEntity.ok(ApiResponse.success(profiles))
    }
    
    @GetMapping("/profiles/high-risk")
    fun getHighRiskProfiles(
        @RequestParam organizationId: UUID,
        @RequestParam(defaultValue = "70.0") threshold: BigDecimal
    ): ResponseEntity<ApiResponse<List<RiskProfile>>> {
        val profiles = riskService.getHighRiskProfiles(organizationId, threshold)
        return ResponseEntity.ok(ApiResponse.success(profiles))
    }
    
    @PostMapping("/metrics/var")
    fun calculateVaR(
        @RequestParam organizationId: UUID,
        @RequestParam(defaultValue = "0.95") confidenceLevel: BigDecimal,
        @RequestParam(defaultValue = "1") timeHorizon: Int
    ): ResponseEntity<ApiResponse<RiskMetric>> {
        val varMetric = riskService.calculateVaR(organizationId, confidenceLevel, timeHorizon)
        return ResponseEntity.ok(ApiResponse.success(varMetric, "VaR calculated successfully"))
    }
    
    @GetMapping("/metrics/capital-ratio")
    fun getCapitalRatio(
        @RequestParam organizationId: UUID
    ): ResponseEntity<ApiResponse<RiskMetric?>> {
        val capitalRatio = riskService.getCapitalRatio(organizationId)
        return ResponseEntity.ok(ApiResponse.success(capitalRatio))
    }
    
    @GetMapping("/metrics/{metricType}")
    fun getRiskMetrics(
        @PathVariable metricType: String,
        @RequestParam organizationId: UUID,
        @RequestParam(defaultValue = "30") days: Long
    ): ResponseEntity<ApiResponse<List<RiskMetric>>> {
        val metrics = riskService.getRiskMetrics(organizationId, metricType, days)
        return ResponseEntity.ok(ApiResponse.success(metrics))
    }
    
    @GetMapping("/summary")
    fun getRiskSummary(
        @RequestParam organizationId: UUID
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val summary = riskService.getRiskSummary(organizationId)
        return ResponseEntity.ok(ApiResponse.success(summary))
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("Risk Service is healthy")
    }
}
