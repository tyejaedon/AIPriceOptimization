package com.tyejaedon.aipriceoptimization.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTOs mirroring the documented FastAPI contract exactly (see
 * docs/API_Contract.md). Do not rename fields without updating the server
 * documentation reference first; @SerialName preserves the wire format
 * while keeping idiomatic Kotlin property names.
 */
@Serializable
data class OptimizePriceRequestDto(
    @SerialName("raw_description")
    val rawDescription: String,

    @SerialName("selected_industry")
    val selectedIndustry: String,

    @SerialName("mentor_country")
    val mentorCountry: String,

    @SerialName("client_country")
    val clientCountry: String,

    @SerialName("competitiveness_score")
    val competitivenessScore: Double,

    @SerialName("market_saturation_score")
    val marketSaturationScore: Double
)

@Serializable
data class PeerMatchDto(
    @SerialName("peer_index")
    val peerIndex: Int,

    val distance: Double,

    @SerialName("verified_rate")
    val verifiedRate: Double,

    @SerialName("similarity_score")
    val similarityScore: Double
)

@Serializable
data class PredictionResultDto(
    @SerialName("base_predicted_rate")
    val basePredictedRate: Double,

    @SerialName("mpesa_tariff_surcharge")
    val mpesaTariffSurcharge: Double,

    @SerialName("final_quoted_rate")
    val finalQuotedRate: Double,

    val currency: String,

    @SerialName("bilateral_arbitrage_factor")
    val bilateralArbitrageFactor: Double,

    @SerialName("nearest_neighbors")
    val nearestNeighbors: List<PeerMatchDto> = emptyList()
)

@Serializable
data class HealthResponseDto(
    val status: String,

    @SerialName("models_loaded")
    val modelsLoaded: Boolean
)

