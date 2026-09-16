package com.tyejaedon.aipriceoptimization.core.error

/**
 * Stable, UI-facing error categories. Every repository must map transport
 * exceptions (Retrofit/OkHttp/Firebase) into one of these before returning
 * to the domain/presentation layers. See docs/API_Contract.md for the
 * HTTP-status-to-category mapping table.
 */
sealed interface AppError {
    data object NotAuthenticated : AppError
    data object NetworkUnavailable : AppError
    data object Timeout : AppError
    data object InvalidRequest : AppError
    data object ServerUnavailable : AppError
    data object ModelNotReady : AppError
    data object RateLimited : AppError
    data class Unknown(val message: String?) : AppError
}

