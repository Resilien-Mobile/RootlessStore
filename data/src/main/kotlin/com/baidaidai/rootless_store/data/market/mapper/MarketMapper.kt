package com.baidaidai.rootless_store.data.market.mapper

import com.baidaidai.rootless_store.data.market.remote.dto.MarketPageResponseDto
import com.baidaidai.rootless_store.data.market.remote.dto.MarketPaginationDto
import com.baidaidai.rootless_store.domain.market.model.MarketPageResponse
import com.baidaidai.rootless_store.domain.market.model.MarketPaginationConfig

object MarketMapper {

    fun MarketPageResponseDto.toMarketPageResponse(): MarketPageResponse {
        return MarketPageResponse(
            manifests = manifests,
            pagination = pagination.toMarketPaginationConfig()
        )
    }

    fun MarketPaginationDto.toMarketPaginationConfig(): MarketPaginationConfig {
        return MarketPaginationConfig(
            limit = limit,
            hasMore = hasMore
        )
    }

}
