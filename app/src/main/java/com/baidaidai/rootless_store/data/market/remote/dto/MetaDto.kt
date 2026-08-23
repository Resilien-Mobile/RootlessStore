package com.baidaidai.rootless_store.data.market.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MetaDto(
    val limit: Int,
    val hasMore: Boolean
)
