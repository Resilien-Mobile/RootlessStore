package com.baidaidai.rootless_store.domain.execute.model

import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class PluginExecuteStatus(
    val pluginID: String,
    val executeStatus: PluginState,
    val executePID: Int,
    val executeContext: HosterOverallStatus = HosterOverallStatus.LIMITED
)
