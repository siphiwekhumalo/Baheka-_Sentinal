package com.baheka.sentinel.common.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.util.*

/**
 * Base response wrapper for all API responses
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: List<String>? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    val timestamp: Instant = Instant.now(),
    val requestId: String = UUID.randomUUID().toString()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, data = data, message = message)
        }
        
        fun <T> error(message: String, errors: List<String>? = null): ApiResponse<T> {
            return ApiResponse(success = false, message = message, errors = errors)
        }
        
        fun <T> error(errors: List<String>): ApiResponse<T> {
            return ApiResponse(success = false, errors = errors)
        }
    }
}

/**
 * Paginated response wrapper
 */
data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)

/**
 * Base entity with common audit fields
 */
abstract class BaseEntity {
    abstract val id: UUID?
    abstract val createdAt: Instant?
    abstract val updatedAt: Instant?
}

/**
 * Common enums
 */
enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class AlertStatus {
    OPEN, INVESTIGATING, CLOSED, ESCALATED
}

enum class TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, CARD_TRANSACTION
}

enum class Channel {
    ATM, ONLINE, BRANCH, MOBILE, CARD, WIRE
}

/**
 * Audit event for compliance tracking
 */
data class AuditEvent(
    val organizationId: UUID,
    val userId: UUID,
    val action: String,
    val entityType: String,
    val entityId: String,
    val oldValues: Map<String, Any?>? = null,
    val newValues: Map<String, Any?>? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val timestamp: Instant = Instant.now()
)
