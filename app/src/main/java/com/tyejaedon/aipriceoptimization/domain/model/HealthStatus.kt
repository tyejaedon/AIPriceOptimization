package com.tyejaedon.aipriceoptimization.domain.model

/**
 * Domain-level projection of GET /health. Kept intentionally minimal;
 * expand only if the server documents additional fields.
 */
data class HealthStatus(
    val isHealthy: Boolean,
    val modelsLoaded: Boolean
)

