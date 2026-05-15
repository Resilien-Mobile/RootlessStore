package com.baidaidai.rootless_store.domain.market.model

import com.baidaidai.rootless_store.data.market.remote.dto.MetaDTO
import com.baidaidai.rootless_store.domain.plugin.manifest.RootlessStoreManifestCollection

data class PluginPageResponse(
    val data: List<RootlessStoreManifestCollection>,
    val meta: MetaDTO
)

