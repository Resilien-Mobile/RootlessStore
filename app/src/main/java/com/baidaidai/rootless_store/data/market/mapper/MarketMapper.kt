package com.baidaidai.rootless_store.data.market.mapper

import com.baidaidai.rootless_store.data.market.remote.dto.MarketPageResponseDto
import com.baidaidai.rootless_store.domain.market.model.MarketPageResponse

object MarketMapper {

    fun MarketPageResponseDto.toMarketPageResponse(): MarketPageResponse {
        return MarketPageResponse(
            manifests = manifests,
            meta = meta
        )
    }

}
