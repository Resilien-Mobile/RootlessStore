package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PluginSourceInfoDTO(
    val sourceID: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    val sourceAuthenticationInfo: SourceAuthenticationInfoMetaDTO
)