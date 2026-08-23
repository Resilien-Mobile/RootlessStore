package com.baidaidai.rootless_store.domain.execution.model

import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class PluginExecutionStatus(
    val pluginId: String,
    val executionState: PluginState,
    val executionPid: Int,
    val executionContext: HosterOverallStatus = HosterOverallStatus.LIMITED
)
