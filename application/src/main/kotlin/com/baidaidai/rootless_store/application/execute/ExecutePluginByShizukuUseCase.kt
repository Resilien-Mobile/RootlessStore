package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.execution.repository.PluginExecutionRepositoryImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.setting.repository.SettingPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecutePluginByShizukuUseCase @Inject constructor(
    private val pluginExecutionGatewayImpl: PluginExecutionGatewayImpl,
    private val pluginExecutionRepositoryImpl: PluginExecutionRepositoryImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val settingPreferencesRepositoryImpl: SettingPreferencesRepositoryImpl
) {
    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginId: String
    ): Flow<ExecutionResult> {

        val pluginManifestRoom = pluginRepositoryImpl.findPlugin(pluginId)!!
        val shouldMonitor = settingPreferencesRepositoryImpl.observePluginStatusNotificationEnabled().first()

        // Dispatch to PluginExecutionGateway.executePluginWithoutEnvironmentByShizuku
        var pidSaved = false
        val pluginExecutionResult = pluginExecutionGatewayImpl.executePluginWithoutEnvironmentByShizuku(
            pluginEntryPoint = pluginManifestRoom.entryPoint,
            pluginDirectory = pluginManifestRoom.pluginPackageName,
            shouldMonitor = shouldMonitor
        )

        // Handle PID and other status
        return pluginExecutionResult.onEach { executionResult ->
            if (!pidSaved) {
                val pid = parsePid(executionResult.output)
                if (pid != null) {
                    pidSaved = true
                    pluginExecutionRepositoryImpl.createPluginExecution(
                        pluginManifestRoom = pluginManifestRoom,
                        executionPid = pid,
                        executionContext = ExecutionContext.ADB
                    )
                }
            }
        }
    }

    private fun parsePid(line: String): Int? {
        return pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
    }
}
