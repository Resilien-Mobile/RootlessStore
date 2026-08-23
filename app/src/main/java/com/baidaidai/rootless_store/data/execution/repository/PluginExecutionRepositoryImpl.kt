package com.baidaidai.rootless_store.data.execution.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.execution.mapper.PluginExecutionMapper.toPluginExecutionStatus
import com.baidaidai.rootless_store.domain.execution.model.PluginExecutionStatus
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import javax.inject.Inject

class PluginExecutionRepositoryImpl @Inject constructor(
    private val pluginExecutionGatewayImpl: PluginExecutionGatewayImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase
) {

    private val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()

    suspend fun abortPluginProcess(pluginManifestRoom: PluginManifestRoom){
        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
        val pidSaved = pluginExecutionDao.findPluginExecutionPidByPluginId(pluginManifestRoom.pluginId)
        pluginExecutionGatewayImpl.abortPluginProcess(pidSaved)
    }

    suspend fun abortPluginProcessByShizuku(pluginManifestRoom: PluginManifestRoom){
        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
        val pidSaved = pluginExecutionDao.findPluginExecutionPidByPluginId(pluginManifestRoom.pluginId)
        pluginExecutionGatewayImpl.abortPluginProcessByShizuku(pidSaved)
    }

    // Create
    // Update
    // Read
    suspend fun listPluginExecutionStatuses(): List<PluginExecutionStatus> {
        val pluginExecutionEntityList = pluginExecutionDao.listPluginExecutions()
        val pluginExecutionStatusList = pluginExecutionEntityList
            .map { pluginExecutionEntity ->
                pluginExecutionEntity.toPluginExecutionStatus()
            }
        return pluginExecutionStatusList
    }

    // Delete
    suspend fun deletePluginExecutionByPluginId(pluginId: String) {
        pluginExecutionDao.deletePluginExecutionByPluginId(pluginId)
    }

    suspend fun deleteAllPluginExecutions() {
        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
        pluginExecutionDao.deleteAllPluginExecutions()
    }
}
