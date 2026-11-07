package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.domain.execution.gateway.PluginExecutionGateway
import com.baidaidai.rootless_store.domain.execution.repository.PluginExecutionRepository
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecutePluginByShizukuUseCase @Inject constructor(
    private val pluginExecutionGateway: PluginExecutionGateway,
    private val pluginExecutionRepository: PluginExecutionRepository,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val settingPreferencesRepository: SettingPreferencesRepository
) {
    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginId: String
    ): Flow<ExecutionResult> {

        val pluginManifest = pluginRepositoryImpl.findPlugin(pluginId)!!
        val shouldMonitor = settingPreferencesRepository.observePluginStatusNotificationEnabled().first()

        // Dispatch to PluginExecutionGateway.executePluginByShizuku
        var pidSaved = false
        val pluginExecutionResult = pluginExecutionGateway.executePluginByShizuku(
            pluginEntryPoint = pluginManifest.entryPoint,
            pluginDirectory = pluginManifest.pluginPackageName,
            shouldMonitor = shouldMonitor
        )

        // Handle PID and other status
        return pluginExecutionResult.onEach { executionResult ->
            if (!pidSaved) {
                val pid = parsePid(executionResult.output)
                if (pid != null) {
                    pidSaved = true
                    pluginExecutionRepository.createPluginExecution(
                        pluginManifest = pluginManifest,
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
