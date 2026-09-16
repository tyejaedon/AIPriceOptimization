package com.tyejaedon.aipriceoptimization.domain.repository

import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.domain.model.HealthStatus

interface HealthRepository {
    suspend fun getHealthStatus(): AppResult<HealthStatus>
}

