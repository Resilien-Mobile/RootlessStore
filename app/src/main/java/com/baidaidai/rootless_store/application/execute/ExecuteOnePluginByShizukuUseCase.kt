package com.baidaidai.rootless_store.application.execute

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusEntry
import com.baidaidai.rootless_store.data.execute.gateway.PluginExecuteGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.data.setting.repository.SettingPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ExecuteOnePluginByShizukuUseCase @Inject constructor(
    private val pluginExecuteGatewayImpl: PluginExecuteGatewayImpl,
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val settingPreferenceRepositoryImpl: SettingPreferenceRepositoryImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase
) {
    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    suspend operator fun invoke(
        pluginID: String
    ): Flow<ExecuteResult> {

        val pluginManifestRoom = pluginRepositoryImpl.getOnePluginInfo(pluginID)!!
        val enableMonitor = settingPreferenceRepositoryImpl.getEnableNotifyPluginStatus().first()

        // Dispatch to PluginExecuteGateway.executePluginWithoutEnvironmentByShizuku
        var pidSaved = false
        val pluginExecuteResult = pluginExecuteGatewayImpl.executePluginWithoutEnvironmentByShizuku(
            pluginEntryPoint = pluginManifestRoom.entryPoint,
            pluginDirectory = pluginManifestRoom.pluginPackageName,
            enableMonitor = enableMonitor
        )

        // Handle PID and other status
        return pluginExecuteResult.onEach { executeResult ->
            if (!pidSaved) {
                val pid = parsePid(executeResult.content)
                if (pid != null) {
                    pidSaved = true
                    val pluginExecuteStatusEntry = PluginExecuteStatusEntry
                        .fromPluginManifest(pluginManifestRoom, pid)
                        .copy(executeContext = HosterOverallStatus.ADB)
                    rootlessStoreDatabase
                        .pluginExecuteStatusDao()
                        .insertOnePluginExecuteStatus(pluginExecuteStatusEntry)
                }
            }
        }
    }

    private fun parsePid(line: String): Int? {
        return pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()
    }
}
