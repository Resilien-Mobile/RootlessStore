package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.setting.repository.SettingPreferencesRepositoryImpl
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecutePluginByAppShellUseCase @Inject constructor(
    private val pluginExecutionGatewayImpl: PluginExecutionGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase,
    private val settingPreferencesRepositoryImpl: SettingPreferencesRepositoryImpl
) {

    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginId: String
    ) : Flow<ExecutionResult> {

        val pluginManifestRoom = pluginRepositoryImpl.findPlugin(pluginId)!!
        val shouldMonitor = settingPreferencesRepositoryImpl.observePluginStatusNotificationEnabled().first()

        var pidSaved = false
        val pluginEntryPoint = androidFileSystemCapabilityGatewayImpl.resolvePluginEntryPoint(pluginManifestRoom)
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.resolvePluginPackageDirectory(pluginManifestRoom)
        return pluginExecutionGatewayImpl
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
                        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
                        val pluginExecutionEntity = PluginExecutionEntity.fromPluginManifest(pluginManifestRoom,pid)
                        pluginExecutionDao.insertPluginExecution(pluginExecutionEntity) // 写 DAO
                    }
                }
            }
    }

    private fun parsePid(line: String): Int? {
        return pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
    }

}
