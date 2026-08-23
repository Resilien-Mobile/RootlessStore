package com.baidaidai.rootless_store.data.execution.mapper

import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.domain.execution.model.PluginExecutionStatus

object PluginExecutionMapper {

    fun PluginExecutionEntity.toPluginExecutionStatus(): PluginExecutionStatus {
        return PluginExecutionStatus(
            pluginID = pluginID,
            executionState = executionState,
            executionPid = executionPid,
            executionContext = executionContext
        )
    }

}
