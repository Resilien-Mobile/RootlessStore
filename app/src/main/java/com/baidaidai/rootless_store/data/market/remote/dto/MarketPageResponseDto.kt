package com.baidaidai.rootless_store.data.market.remote.dto

import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketPageResponseDto(
    @SerialName("data")
    val manifests: List<MarketManifest>,
    val meta: MetaDto
)
