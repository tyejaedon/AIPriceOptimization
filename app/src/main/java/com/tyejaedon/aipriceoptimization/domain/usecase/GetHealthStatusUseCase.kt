package com.tyejaedon.aipriceoptimization.domain.usecase

import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.domain.model.HealthStatus
import com.tyejaedon.aipriceoptimization.domain.repository.HealthRepository
import javax.inject.Inject

class GetHealthStatusUseCase @Inject constructor(
    private val healthRepository: HealthRepository
) {
    suspend operator fun invoke(): AppResult<HealthStatus> = healthRepository.getHealthStatus()
}

