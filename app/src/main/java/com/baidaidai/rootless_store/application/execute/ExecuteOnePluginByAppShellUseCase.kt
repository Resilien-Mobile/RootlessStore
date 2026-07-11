package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusEntry
import com.baidaidai.rootless_store.data.execute.gateway.PluginExecuteGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecuteOnePluginByAppShellUseCase @Inject constructor(
    private val pluginExecuteGatewayImpl: PluginExecuteGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase,
    private val settingPreferenceRepositoryImpl: SettingPreferenceRepositoryImpl
) {

    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginID: String
    ) : Flow<ExecuteResult> {

        val pluginManifestRoom = pluginRepositoryImpl.getOnePluginInfo(pluginID)!!
        val enableMonitor = settingPreferenceRepositoryImpl.getEnableNotifyPluginStatus().first()

        var pidSaved = false
        val pluginExecuteEntryPoint = androidFileSystemCapabilityGatewayImpl.getPluginEntryPoint(pluginManifestRoom)
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.getPluginPackageDirectory(pluginManifestRoom)
        return pluginExecuteGatewayImpl
            .executePluginEntryPoint(
                pluginExecuteEntryPoint,
                pluginPackageDirectory,
                enableMonitor = enableMonitor
            )
            .onEach { ExecuteResult ->
                if (!pidSaved) {
                    val content = ExecuteResult.content
                    val pid = parsePid(content)
                    if (pid != null) {
                        pidSaved = true
                        val pluginExecuteStatusDao = rootlessStoreDatabase.pluginExecuteStatusDao()
                        val pluginExecuteStatusEntry = PluginExecuteStatusEntry.fromPluginManifest(pluginManifestRoom,pid)
                        pluginExecuteStatusDao.insertOnePluginExecuteStatus(pluginExecuteStatusEntry) // 写 DAO
                    }
                }
            }
    }

    private fun parsePid(line: String): Int? {
        return pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
    }

}