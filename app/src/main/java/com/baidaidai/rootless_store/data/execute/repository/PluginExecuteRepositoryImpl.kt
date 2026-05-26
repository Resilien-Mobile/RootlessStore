package com.baidaidai.rootless_store.data.execute.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusEntry
import com.baidaidai.rootless_store.data.execute.gateway.PluginExecuteGatewayImpl
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PluginExecuteRepositoryImpl @Inject constructor(
    private val pluginExecuteGatewayImpl: PluginExecuteGatewayImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase
) {
    private val pidRegex = Regex("""^\s*-\s*PID:(\d+)\s*$""")

    fun parsePid(line: String): Int? =
        pidRegex.find(line)?.groupValues?.get(1)?.toIntOrNull()

    fun executeOnePlugin(
        pluginManifestRoom: PluginManifestRoom,
        enableMonitor: Boolean = false
    ): Flow<ExecuteResult> {
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

    fun executeOnePluginByShizuku(
        pluginManifestRoom: PluginManifestRoom,
        enableMonitor: Boolean = false
    ): Flow<ExecuteResult> {
        var pidSaved = false
        val pluginExecuteEntryPoint = androidFileSystemCapabilityGatewayImpl.getPluginEntryPoint(pluginManifestRoom)
        val pluginPackageDirectory = androidFileSystemCapabilityGatewayImpl.getPluginPackageDirectory(pluginManifestRoom)
        return pluginExecuteGatewayImpl
            .executePluginEntryPointByShizuku(
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


    suspend fun abortPluginProcess(pluginManifestRoom: PluginManifestRoom){
        val pluginExecuteStatusDao = rootlessStoreDatabase.pluginExecuteStatusDao()
        val pidSaved = pluginExecuteStatusDao.getPluginExecutePIDByPluginID(pluginManifestRoom.pluginID)
        pluginExecuteGatewayImpl.abortPluginProcess(pidSaved)
    }

    suspend fun abortPluginProcessByShizuku(pluginManifestRoom: PluginManifestRoom){
        val pluginExecuteStatusDao = rootlessStoreDatabase.pluginExecuteStatusDao()
        val pidSaved = pluginExecuteStatusDao.getPluginExecutePIDByPluginID(pluginManifestRoom.pluginID)
        pluginExecuteGatewayImpl.abortPluginProcessByShizuku(pidSaved)
    }
}