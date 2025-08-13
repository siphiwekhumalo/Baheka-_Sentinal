package com.baheka.sentinel.risk.service

import com.baheka.sentinel.risk.entity.ProfileType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.random.Random

@Service
class RiskCalculationService {
    
    /**
     * Calculate risk score using ML model (simplified for MVP)
     * In production, this would call Python ML service or use PMML/ONNX model
     */
    fun calculateRiskScore(organizationId: UUID, customerId: String, profileType: ProfileType): BigDecimal {
        // Simulate ML model prediction
        // In production: call Python FastAPI service or use embedded model
        
        val baseScore = when (profileType) {
            ProfileType.INDIVIDUAL -> calculateIndividualRiskScore(customerId)
            ProfileType.CORPORATE -> calculateCorporateRiskScore(customerId)
        }
        
        // Add some randomness to simulate real-world variability
        val variance = Random.nextDouble(-5.0, 5.0)
        val finalScore = (baseScore + variance).coerceIn(0.0, 100.0)
        
        return BigDecimal(finalScore).setScale(2, RoundingMode.HALF_UP)
    }
    
    /**
     * Calculate Probability of Default (PD) for Basel III
     */
    fun calculatePD(organizationId: UUID, customerId: String): BigDecimal {
        // Simplified PD calculation
        // In production: use sophisticated credit risk models
        
        val riskFactors = getCustomerRiskFactors(customerId)
        
        val pdValue = when {
            riskFactors["creditScore"] as Int > 750 -> 0.01 // 1%
            riskFactors["creditScore"] as Int > 650 -> 0.03 // 3%
            riskFactors["creditScore"] as Int > 550 -> 0.08 // 8%
            else -> 0.15 // 15%
        }
        
        return BigDecimal(pdValue).setScale(6, RoundingMode.HALF_UP)
    }
    
    /**
     * Calculate Loss Given Default (LGD) for Basel III
     */
    fun calculateLGD(organizationId: UUID, customerId: String): BigDecimal {
        // Simplified LGD calculation based on collateral and industry
        val riskFactors = getCustomerRiskFactors(customerId)
        
        val lgdValue = when (riskFactors["hasCollateral"] as Boolean) {
            true -> 0.25 // 25% loss with collateral
            false -> 0.45 // 45% loss without collateral
        }
        
        return BigDecimal(lgdValue).setScale(6, RoundingMode.HALF_UP)
    }
    
    /**
     * Calculate Exposure at Default (EAD) for Basel III
     */
    fun calculateEAD(organizationId: UUID, customerId: String): BigDecimal {
        // Simplified EAD calculation
        val riskFactors = getCustomerRiskFactors(customerId)
        
        val creditLimit = riskFactors["creditLimit"] as Double
        val currentUtilization = riskFactors["utilization"] as Double
        
        // EAD = Current Exposure + (Undrawn Amount * Credit Conversion Factor)
        val currentExposure = creditLimit * currentUtilization
        val undrawnAmount = creditLimit - currentExposure
        val ccf = 0.75 // Credit Conversion Factor
        
        val eadValue = currentExposure + (undrawnAmount * ccf)
        
        return BigDecimal(eadValue).setScale(2, RoundingMode.HALF_UP)
    }
    
    /**
     * Calculate Value at Risk (VaR) for the organization
     */
    fun calculateVaR(organizationId: UUID, confidenceLevel: BigDecimal, timeHorizon: Int): BigDecimal {
        // Simplified VaR calculation using Monte Carlo simulation
        // In production: use sophisticated risk models
        
        val portfolioValue = getPortfolioValue(organizationId)
        val volatility = getPortfolioVolatility(organizationId)
        
        // VaR = Portfolio Value * Z-score * Volatility * sqrt(Time Horizon)
        val zScore = getZScore(confidenceLevel)
        val timeAdjustment = Math.sqrt(timeHorizon.toDouble())
        
        val varValue = portfolioValue * zScore * volatility * timeAdjustment
        
        return BigDecimal(varValue).setScale(2, RoundingMode.HALF_UP)
    }
    
    private fun calculateIndividualRiskScore(customerId: String): Double {
        val factors = getCustomerRiskFactors(customerId)
        
        val creditScore = factors["creditScore"] as Int
        val income = factors["income"] as Double
        val debtToIncome = factors["debtToIncome"] as Double
        val age = factors["age"] as Int
        
        // Simplified scoring algorithm
        var score = 50.0 // Base score
        
        // Credit score impact (40% weight)
        score += when {
            creditScore >= 800 -> -20.0
            creditScore >= 750 -> -10.0
            creditScore >= 650 -> 0.0
            creditScore >= 550 -> 15.0
            else -> 30.0
        } * 0.4
        
        // Income impact (20% weight)
        score += when {
            income >= 100000 -> -10.0
            income >= 50000 -> -5.0
            income >= 25000 -> 0.0
            else -> 10.0
        } * 0.2
        
        // Debt-to-income impact (30% weight)
        score += when {
            debtToIncome <= 0.2 -> -15.0
            debtToIncome <= 0.3 -> -5.0
            debtToIncome <= 0.4 -> 5.0
            else -> 20.0
        } * 0.3
        
        // Age impact (10% weight)
        score += when {
            age >= 40 -> -5.0
            age >= 25 -> 0.0
            else -> 5.0
        } * 0.1
        
        return score.coerceIn(0.0, 100.0)
    }
    
    private fun calculateCorporateRiskScore(customerId: String): Double {
        val factors = getCustomerRiskFactors(customerId)
        
        val revenue = factors["revenue"] as Double
        val profitMargin = factors["profitMargin"] as Double
        val debtToEquity = factors["debtToEquity"] as Double
        val industry = factors["industry"] as String
        
        var score = 40.0 // Base score for corporates
        
        // Revenue stability (25% weight)
        score += when {
            revenue >= 10000000 -> -15.0
            revenue >= 1000000 -> -8.0
            revenue >= 100000 -> 0.0
            else -> 15.0
        } * 0.25
        
        // Profitability (30% weight)
        score += when {
            profitMargin >= 0.15 -> -20.0
            profitMargin >= 0.10 -> -10.0
            profitMargin >= 0.05 -> 0.0
            profitMargin >= 0.0 -> 10.0
            else -> 25.0
        } * 0.3
        
        // Leverage (35% weight)
        score += when {
            debtToEquity <= 0.5 -> -15.0
            debtToEquity <= 1.0 -> -5.0
            debtToEquity <= 2.0 -> 10.0
            else -> 25.0
        } * 0.35
        
        // Industry risk (10% weight)
        score += when (industry) {
            "TECHNOLOGY", "HEALTHCARE" -> -5.0
            "MANUFACTURING", "RETAIL" -> 0.0
            "ENERGY", "MINING" -> 10.0
            else -> 5.0
        } * 0.1
        
        return score.coerceIn(0.0, 100.0)
    }
    
    private fun getCustomerRiskFactors(customerId: String): Map<String, Any> {
        // Simulate customer data retrieval
        // In production: fetch from customer database or external data sources
        
        val hash = customerId.hashCode()
        val random = Random(hash)
        
        return mapOf(
            "creditScore" to (random.nextInt(300, 850)),
            "income" to (random.nextDouble(20000.0, 200000.0)),
            "debtToIncome" to (random.nextDouble(0.1, 0.8)),
            "age" to (random.nextInt(18, 80)),
            "hasCollateral" to random.nextBoolean(),
            "creditLimit" to (random.nextDouble(10000.0, 500000.0)),
            "utilization" to (random.nextDouble(0.1, 0.9)),
            "revenue" to (random.nextDouble(50000.0, 50000000.0)),
            "profitMargin" to (random.nextDouble(-0.1, 0.3)),
            "debtToEquity" to (random.nextDouble(0.1, 4.0)),
            "industry" to listOf("TECHNOLOGY", "HEALTHCARE", "MANUFACTURING", "RETAIL", "ENERGY", "MINING").random()
        )
    }
    
    private fun getPortfolioValue(organizationId: UUID): Double {
        // Simulate portfolio value retrieval
        return Random.nextDouble(1000000.0, 1000000000.0)
    }
    
    private fun getPortfolioVolatility(organizationId: UUID): Double {
        // Simulate portfolio volatility calculation
        return Random.nextDouble(0.15, 0.35)
    }
    
    private fun getZScore(confidenceLevel: BigDecimal): Double {
        // Z-scores for common confidence levels
        return when (confidenceLevel.toDouble()) {
            0.90 -> 1.28
            0.95 -> 1.65
            0.99 -> 2.33
            else -> 1.65 // Default to 95%
        }
    }
}
