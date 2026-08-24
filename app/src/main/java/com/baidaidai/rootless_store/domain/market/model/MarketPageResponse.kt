package com.baidaidai.rootless_store.domain.market.model

import com.baidaidai.rootless_store.data.market.remote.dto.MarketPaginationDto

data class MarketPageResponse(
    val manifests: List<MarketManifest>,
    val pagination: MarketPaginationDto
)
