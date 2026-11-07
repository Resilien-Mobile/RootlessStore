package com.baidaidai.rootless_store.domain.execution.gateway

import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import kotlinx.coroutines.flow.Flow

interface PluginExecutionGateway {

    fun executePluginEntryPoint(
        pluginEntryPoint: String,
        pluginPackageDirectory: String,
        shouldMonitor: Boolean = false
    ): Flow<ExecutionResult>

    fun executePluginByShizuku(
        pluginDirectory: String,
        pluginEntryPoint: String,
        shouldMonitor: Boolean
    ): Flow<ExecutionResult>

    fun abortPluginProcess(
        pluginProcessPid: Int?
    )

    fun abortPluginProcessByShizuku(
        pluginProcessPid: Int?
    ): Boolean

    fun resolveLocalShellExecutable(): String

    fun commandFactory(
        shellExecutable: String,
        pluginEntryPoint: String,
        pluginPackageDirectory: String
    ): List<String>

}
