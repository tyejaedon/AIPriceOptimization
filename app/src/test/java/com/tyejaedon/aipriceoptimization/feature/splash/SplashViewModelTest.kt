package com.tyejaedon.aipriceoptimization.feature.splash

import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.core.error.AppError
import com.tyejaedon.aipriceoptimization.domain.model.HealthStatus
import com.tyejaedon.aipriceoptimization.domain.usecase.GetHealthStatusUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SplashViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val getHealthStatus = mockk<GetHealthStatusUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `reaches Ready state after a successful health check`() = runTest {
        coEvery { getHealthStatus() } returns AppResult.Success(
            HealthStatus(isHealthy = true, modelsLoaded = true)
        )

        val viewModel = SplashViewModel(getHealthStatus)

        assertEquals(SplashState.Ready, viewModel.state.value)
    }

    @Test
    fun `reaches Ready state even when the health check reports a failure`() = runTest {
        // Splash never blocks indefinitely on API health - it must still
        // reach Ready so the user is not stranded.
        coEvery { getHealthStatus() } returns AppResult.Failure(AppError.NetworkUnavailable)

        val viewModel = SplashViewModel(getHealthStatus)

        assertEquals(SplashState.Ready, viewModel.state.value)
    }
}

