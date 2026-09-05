package com.baidaidai.rootless_store.data.market.remote.dto

import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import kotlinx.serialization.Serializable

@Serializable
data class MarketPageResponseDto(
    val manifests: List<MarketManifest>,
    val pagination: MarketPaginationDto
)
