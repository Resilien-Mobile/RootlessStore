package com.baidaidai.rootless_store.data.execute.mapper

import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusEntry
import com.baidaidai.rootless_store.domain.execute.model.PluginExecuteStatus

object PluginExecuteMapper {

    fun PluginExecuteStatusEntry.toPluginExecuteStatus(): PluginExecuteStatus {
        return PluginExecuteStatus(
            pluginID = pluginID,
            executeStatus = executeStatus,
            executePID = executePID,
            executeContext = executeContext
        )
    }

}
