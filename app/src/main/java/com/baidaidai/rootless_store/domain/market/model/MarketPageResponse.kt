package com.baidaidai.rootless_store.domain.market.model

import com.baidaidai.rootless_store.data.market.remote.dto.MetaDto

data class MarketPageResponse(
    val manifests: List<MarketManifest>,
    val meta: MetaDto
)
