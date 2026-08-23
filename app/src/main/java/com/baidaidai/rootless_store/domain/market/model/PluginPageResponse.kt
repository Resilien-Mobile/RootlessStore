package com.baidaidai.rootless_store.domain.market.model

import com.baidaidai.rootless_store.data.market.remote.dto.MetaDto
import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection

data class PluginPageResponse(
    val data: List<ModuleManifestCollection>,
    val meta: MetaDto
)

