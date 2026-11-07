package com.baidaidai.rootless_store.domain.execution.repository

import com.baidaidai.rootless_store.domain.execution.model.PluginExecutionStatus
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

interface PluginExecutionRepository {

    suspend fun abortPluginProcess(
        pluginId: String
    )

    suspend fun abortPluginProcessByShizuku(
        pluginId: String
    )

    suspend fun createPluginExecution(
        pluginManifest: PluginManifest,
        executionPid: Int,
        executionContext: ExecutionContext = ExecutionContext.LIMITED
    )

    suspend fun listPluginExecutionStatuses(): List<PluginExecutionStatus>

    suspend fun deletePluginExecutionByPluginId(
        pluginId: String
    )

    suspend fun deleteAllPluginExecutions()

}
