package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.domain.execution.gateway.PluginExecutionGateway
import com.baidaidai.rootless_store.domain.execution.repository.PluginExecutionRepository
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.setting.repository.SettingPreferencesRepository
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecutePluginByAppShellUseCase @Inject constructor(
    private val pluginExecutionGateway: PluginExecutionGateway,
    private val pluginExecutionRepository: PluginExecutionRepository,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val settingPreferencesRepository: SettingPreferencesRepository
) {

    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginId: String
    ) : Flow<ExecutionResult> {

        val pluginManifest = pluginRepositoryImpl.findPlugin(pluginId)!!
        val shouldMonitor = settingPreferencesRepository.observePluginStatusNotificationEnabled().first()

        var pidSaved = false
        val pluginEntryPoint = androidFileSystemCapabilityGatewayImpl.resolvePluginEntryPoint(pluginManifest)
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.resolvePluginPackageDirectory(pluginManifest)
        return pluginExecutionGateway
            .executePluginEntryPoint(
                pluginEntryPoint,
                pluginPackageDirectory,
                shouldMonitor = shouldMonitor
            )
            .onEach { executionResult ->
                if (!pidSaved) {
                    val output = executionResult.output
                    val pid = parsePid(output)
                    if (pid != null) {
                        pidSaved = true
                        pluginExecutionRepository.createPluginExecution(
                            pluginManifest = pluginManifest,
                            executionPid = pid
                        )
                    }
                }
            }
    }

    private fun parsePid(line: String): Int? {
        return pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
    }

}
