package com.tyejaedon.aipriceoptimization.data.remote

import com.tyejaedon.aipriceoptimization.data.remote.dto.HealthResponseDto
import com.tyejaedon.aipriceoptimization.data.remote.dto.OptimizePriceRequestDto
import com.tyejaedon.aipriceoptimization.data.remote.dto.PredictionResultDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit interface for the documented backend contract.
 *
 * Only these two endpoints are confirmed to exist server-side today:
 *   GET  /health
 *   POST /api/v1/optimize-price
 *
 * Do not add speculative endpoints here without confirming them against the
 * backend repository first (see docs/API_Contract.md, "Undocumented
 * endpoints").
 */
interface PricingApi {

    @GET("health")
    suspend fun getHealth(): HealthResponseDto

    @POST("api/v1/optimize-price")
    suspend fun optimizePrice(
        @Body request: OptimizePriceRequestDto
    ): PredictionResultDto
}

