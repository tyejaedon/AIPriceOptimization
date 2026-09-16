package com.tyejaedon.aipriceoptimization.feature.dashboard

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DashboardViewModelTest {

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
    fun `initial refresh reflects a healthy service as available`() = runTest {
        coEvery { getHealthStatus() } returns AppResult.Success(
            HealthStatus(isHealthy = true, modelsLoaded = true)
        )

        val viewModel = DashboardViewModel(getHealthStatus)
        val state = viewModel.uiState.value

        assertFalse(state.isCheckingHealth)
        assertEquals(true, state.isServiceAvailable)
        assertNull(state.error)
    }

    @Test
    fun `initial refresh reflects models not loaded as unavailable`() = runTest {
        coEvery { getHealthStatus() } returns AppResult.Success(
            HealthStatus(isHealthy = true, modelsLoaded = false)
        )

        val viewModel = DashboardViewModel(getHealthStatus)
        val state = viewModel.uiState.value

        assertEquals(false, state.isServiceAvailable)
    }

    @Test
    fun `initial refresh maps failure to unavailable with error`() = runTest {
        coEvery { getHealthStatus() } returns AppResult.Failure(AppError.ServerUnavailable)

        val viewModel = DashboardViewModel(getHealthStatus)
        val state = viewModel.uiState.value

        assertFalse(state.isCheckingHealth)
        assertEquals(false, state.isServiceAvailable)
        assertEquals(AppError.ServerUnavailable, state.error)
    }

    @Test
    fun `refreshHealth can be called again and updates state`() = runTest {
        coEvery { getHealthStatus() } returns AppResult.Failure(AppError.NetworkUnavailable)
        val viewModel = DashboardViewModel(getHealthStatus)

        coEvery { getHealthStatus() } returns AppResult.Success(
            HealthStatus(isHealthy = true, modelsLoaded = true)
        )
        viewModel.refreshHealth()

        val state = viewModel.uiState.value
        assertEquals(true, state.isServiceAvailable)
        assertNull(state.error)
    }
}

