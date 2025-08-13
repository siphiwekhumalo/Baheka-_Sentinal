package com.baheka.sentinel.aml.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "transactions", schema = "sentinel_aml")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    
    @Column(name = "organization_id", nullable = false)
    val organizationId: UUID,
    
    @Column(name = "transaction_id", nullable = false)
    val transactionId: String,
    
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    val amount: BigDecimal,
    
    @Column(name = "currency", nullable = false, length = 3)
    val currency: String,
    
    @Column(name = "sender_account", nullable = false)
    val senderAccount: String,
    
    @Column(name = "receiver_account", nullable = false)
    val receiverAccount: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    val channel: Channel,
    
    @Column(name = "country_code", length = 3)
    val countryCode: String? = null,
    
    @Column(name = "processed_at", nullable = false)
    val processedAt: Instant,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)

enum class TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, CARD_TRANSACTION
}

enum class Channel {
    ATM, ONLINE, BRANCH, MOBILE, CARD, WIRE
}

@Entity
@Table(name = "alerts", schema = "sentinel_aml")
data class AmlAlert(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    
    @Column(name = "organization_id", nullable = false)
    val organizationId: UUID,
    
    @Column(name = "transaction_id")
    val transactionId: UUID? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    val alertType: AlertType,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    val severity: Severity,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: AlertStatus = AlertStatus.OPEN,
    
    @Column(name = "assigned_to")
    val assignedTo: UUID? = null,
    
    @Column(name = "rule_triggered")
    val ruleTriggered: String? = null,
    
    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
)

enum class AlertType {
    SUSPICIOUS_TRANSACTION, VELOCITY_CHECK, STRUCTURING, 
    WATCHLIST_MATCH, GEOGRAPHIC_ANOMALY, LARGE_CASH_TRANSACTION,
    UNUSUAL_PATTERN, PEP_MATCH, SANCTIONS_MATCH
}

enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class AlertStatus {
    OPEN, INVESTIGATING, CLOSED, ESCALATED
}

@Entity
@Table(name = "watchlist_matches", schema = "sentinel_aml")
data class WatchlistMatch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    
    @Column(name = "organization_id", nullable = false)
    val organizationId: UUID,
    
    @Column(name = "entity_name", nullable = false)
    val entityName: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    val entityType: EntityType,
    
    @Column(name = "watchlist_source", nullable = false)
    val watchlistSource: String, // OFAC, UN, EU, FIC_SA
    
    @Column(name = "match_score", nullable = false, precision = 5, scale = 2)
    val matchScore: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: MatchStatus = MatchStatus.PENDING,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
)

enum class EntityType {
    INDIVIDUAL, ORGANIZATION
}

enum class MatchStatus {
    PENDING, CONFIRMED, FALSE_POSITIVE
}
