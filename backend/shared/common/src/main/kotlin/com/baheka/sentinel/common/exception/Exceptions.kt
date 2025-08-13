package com.baheka.sentinel.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

/**
 * Base exception for all Baheka Sentinel exceptions
 */
abstract class SentinelException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Business logic exceptions
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationException(message: String) : SentinelException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(message: String) : SentinelException(message) {
    constructor(entity: String, id: UUID) : this("$entity with id $id not found")
    constructor(entity: String, field: String, value: String) : this("$entity with $field '$value' not found")
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class AccessDeniedException(message: String) : SentinelException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class AuthenticationException(message: String) : SentinelException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class DuplicateResourceException(message: String) : SentinelException(message)

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class BusinessRuleViolationException(message: String) : SentinelException(message)

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ExternalServiceException(message: String, cause: Throwable? = null) : SentinelException(message, cause)

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
class RateLimitExceededException(message: String) : SentinelException(message)

/**
 * Domain-specific exceptions
 */
class RiskCalculationException(message: String, cause: Throwable? = null) : SentinelException(message, cause)

class AmlScreeningException(message: String, cause: Throwable? = null) : SentinelException(message, cause)

class ComplianceViolationException(message: String) : SentinelException(message)

class SecurityPolicyViolationException(message: String) : SentinelException(message)
