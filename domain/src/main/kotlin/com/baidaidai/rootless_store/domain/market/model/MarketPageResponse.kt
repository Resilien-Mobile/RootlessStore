package com.baidaidai.rootless_store.domain.market.model


data class MarketPageResponse(
    val manifests: List<MarketManifest>,
    val pagination: MarketPaginationConfig
)