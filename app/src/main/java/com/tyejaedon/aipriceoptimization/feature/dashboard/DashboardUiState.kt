package com.tyejaedon.aipriceoptimization.feature.dashboard

import com.tyejaedon.aipriceoptimization.core.error.AppError

data class DashboardUiState(
    val isCheckingHealth: Boolean = true,
    val isServiceAvailable: Boolean? = null,
    val error: AppError? = null
)

