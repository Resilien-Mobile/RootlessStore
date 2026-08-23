package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceInfoDto(
    @SerialName("sourceID")
    val sourceId: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    val sourceAuthenticationInfo: SourceAuthenticationInfoMetaDto
)
