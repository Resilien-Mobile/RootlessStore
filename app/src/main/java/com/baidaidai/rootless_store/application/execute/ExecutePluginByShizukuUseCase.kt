package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecutePluginByShizukuUseCase @Inject constructor(
    private val pluginExecutionGatewayImpl: PluginExecutionGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val settingPreferenceRepositoryImpl: SettingPreferenceRepositoryImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase
) {
    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginId: String
    ): Flow<ExecutionResult> {

        val pluginManifestRoom = pluginRepositoryImpl.findPluginInfo(pluginId)!!
        val enableMonitor = settingPreferenceRepositoryImpl.observeEnableNotifyPluginStatus().first()

        // Dispatch to PluginExecutionGateway.executePluginWithoutEnvironmentByShizuku
        var pidSaved = false
        val pluginExecutionResult = pluginExecutionGatewayImpl.executePluginWithoutEnvironmentByShizuku(
            pluginEntryPoint = pluginManifestRoom.entryPoint,
            pluginDirectory = pluginManifestRoom.pluginPackageName,
            enableMonitor = enableMonitor
        )

        // Handle PID and other status
        return pluginExecutionResult.onEach { executionResult ->
            if (!pidSaved) {
                val pid = parsePid(executionResult.content)
                if (pid != null) {
                    pidSaved = true
                    val pluginExecutionEntity = PluginExecutionEntity
                        .fromPluginManifest(pluginManifestRoom, pid)
                        .copy(executionContext = HosterOverallStatus.ADB)
                    rootlessStoreDatabase
                        .pluginExecutionDao()
                        .insertPluginExecution(pluginExecutionEntity)
                }
            }
        }
    }

    private fun parsePid(line: String): Int? {
        return pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
    }
}
