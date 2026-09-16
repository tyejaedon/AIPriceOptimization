package com.tyejaedon.aipriceoptimization.core.common

import com.tyejaedon.aipriceoptimization.core.error.AppError

/**
 * Generic result wrapper used across the domain layer so use cases and
 * repositories never leak Retrofit/Room/Firebase-specific exception types
 * into the presentation layer.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(value)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

