package com.baidaidai.rootless_store.data.market.remote.dto

import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection
import kotlinx.serialization.Serializable

@Serializable
data class PluginPageResponseDTO(
    val data: List<ModuleManifestCollection>,
    val meta: MetaDTO
)