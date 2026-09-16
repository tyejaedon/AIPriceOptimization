package com.tyejaedon.aipriceoptimization.data.repository

import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.core.error.AppError
import com.tyejaedon.aipriceoptimization.data.remote.PricingApi
import com.tyejaedon.aipriceoptimization.data.remote.dto.HealthResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class HealthRepositoryImplTest {

    private val api = mockk<PricingApi>()
    private val repository = HealthRepositoryImpl(api)

    @Test
    fun `getHealthStatus maps healthy response to success`() = runTest {
        coEvery { api.getHealth() } returns HealthResponseDto(status = "HEALTHY", modelsLoaded = true)

        val result = repository.getHealthStatus()

        assertTrue(result is AppResult.Success)
        val status = (result as AppResult.Success).value
        assertTrue(status.isHealthy)
        assertTrue(status.modelsLoaded)
    }

    @Test
    fun `getHealthStatus maps non-healthy status to isHealthy false`() = runTest {
        coEvery { api.getHealth() } returns HealthResponseDto(status = "DEGRADED", modelsLoaded = false)

        val result = repository.getHealthStatus() as AppResult.Success

        assertFalse(result.value.isHealthy)
        assertFalse(result.value.modelsLoaded)
    }

    @Test
    fun `getHealthStatus maps SocketTimeoutException to Timeout`() = runTest {
        coEvery { api.getHealth() } throws SocketTimeoutException()

        val result = repository.getHealthStatus()

        assertEquals(AppError.Timeout, (result as AppResult.Failure).error)
    }

    @Test
    fun `getHealthStatus maps IOException to NetworkUnavailable`() = runTest {
        coEvery { api.getHealth() } throws IOException()

        val result = repository.getHealthStatus()

        assertEquals(AppError.NetworkUnavailable, (result as AppResult.Failure).error)
    }

    @Test
    fun `getHealthStatus maps HTTP 503 to ServerUnavailable`() = runTest {
        val httpException = HttpException(
            Response.error<Any>(503, "".toResponseBody("application/json".toMediaType()))
        )
        coEvery { api.getHealth() } throws httpException

        val result = repository.getHealthStatus()

        assertEquals(AppError.ServerUnavailable, (result as AppResult.Failure).error)
    }

    @Test
    fun `getHealthStatus maps HTTP 401 to NotAuthenticated`() = runTest {
        val httpException = HttpException(
            Response.error<Any>(401, "".toResponseBody("application/json".toMediaType()))
        )
        coEvery { api.getHealth() } throws httpException

        val result = repository.getHealthStatus()

        assertEquals(AppError.NotAuthenticated, (result as AppResult.Failure).error)
    }
}

