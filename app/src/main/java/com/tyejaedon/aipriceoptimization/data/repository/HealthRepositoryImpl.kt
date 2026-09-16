package com.tyejaedon.aipriceoptimization.data.repository

import com.tyejaedon.aipriceoptimization.core.common.AppResult
import com.tyejaedon.aipriceoptimization.core.error.AppError
import com.tyejaedon.aipriceoptimization.data.remote.PricingApi
import com.tyejaedon.aipriceoptimization.domain.model.HealthStatus
import com.tyejaedon.aipriceoptimization.domain.repository.HealthRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val pricingApi: PricingApi
) : HealthRepository {

    override suspend fun getHealthStatus(): AppResult<HealthStatus> = try {
        val response = pricingApi.getHealth()
        AppResult.Success(
            HealthStatus(
                isHealthy = response.status.equals("HEALTHY", ignoreCase = true),
                modelsLoaded = response.modelsLoaded
            )
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: SocketTimeoutException) {
        AppResult.Failure(AppError.Timeout)
    } catch (e: HttpException) {
        AppResult.Failure(mapHttpError(e.code()))
    } catch (e: IOException) {
        AppResult.Failure(AppError.NetworkUnavailable)
    } catch (e: Exception) {
        AppResult.Failure(AppError.Unknown(e.message))
    }

    private fun mapHttpError(code: Int): AppError = when (code) {
        401 -> AppError.NotAuthenticated
        403 -> AppError.NotAuthenticated
        422 -> AppError.InvalidRequest
        429 -> AppError.RateLimited
        503 -> AppError.ServerUnavailable
        else -> AppError.Unknown("HTTP $code")
    }
}

