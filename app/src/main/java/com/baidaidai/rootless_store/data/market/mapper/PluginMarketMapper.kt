package com.baidaidai.rootless_store.data.market.mapper

import com.baidaidai.rootless_store.data.market.remote.dto.PluginPageResponseDTO
import com.baidaidai.rootless_store.domain.market.model.PluginPageResponse

object PluginMarketMapper {

    fun PluginPageResponseDTO.toPluginPageResponse(): PluginPageResponse {
        return PluginPageResponse(data,meta)
    }

}