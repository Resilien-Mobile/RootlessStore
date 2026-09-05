package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceDto(
    val sourceId: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    val authenticationMetadata: PluginSourceAuthenticationMetadataDto
)
