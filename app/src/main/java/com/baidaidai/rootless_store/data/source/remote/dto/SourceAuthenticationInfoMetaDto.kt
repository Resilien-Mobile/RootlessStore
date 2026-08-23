package com.baidaidai.rootless_store.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceAuthenticationInfoMetaDto(
    @SerialName("requireAuthentication")
    val needsAuthentication: Boolean
)
