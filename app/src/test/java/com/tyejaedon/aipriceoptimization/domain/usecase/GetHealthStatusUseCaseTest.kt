package com.tyejaedon.aipriceoptimization.domain.usecase

import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.domain.model.HealthStatus
import com.tyejaedon.aipriceoptimization.domain.repository.HealthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetHealthStatusUseCaseTest {

    private val repository = mockk<HealthRepository>()
    private val useCase = GetHealthStatusUseCase(repository)

    @Test
    fun `invoke delegates to repository and returns its result`() = runTest {
        val expected = AppResult.Success(HealthStatus(isHealthy = true, modelsLoaded = true))
        coEvery { repository.getHealthStatus() } returns expected

        val result = useCase()

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getHealthStatus() }
    }
}

