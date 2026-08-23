package com.baidaidai.rootless_store.data.execute.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execute.gateway.PluginExecuteGatewayImpl
import com.baidaidai.rootless_store.data.execute.mapper.PluginExecuteMapper.toPluginExecuteStatus
import com.baidaidai.rootless_store.domain.execute.model.PluginExecuteStatus
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import javax.inject.Inject

class PluginExecuteRepositoryImpl @Inject constructor(
    private val pluginExecuteGatewayImpl: PluginExecuteGatewayImpl,
    private val rootlessStoreDatabase: RootlessStoreDatabase
) {

    private val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()

    suspend fun abortPluginProcess(pluginManifestRoom: PluginManifestRoom){
        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
        val pidSaved = pluginExecutionDao.findPluginExecutionPidByPluginId(pluginManifestRoom.pluginID)
        pluginExecuteGatewayImpl.abortPluginProcess(pidSaved)
    }

    suspend fun abortPluginProcessByShizuku(pluginManifestRoom: PluginManifestRoom){
        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
        val pidSaved = pluginExecutionDao.findPluginExecutionPidByPluginId(pluginManifestRoom.pluginID)
        pluginExecuteGatewayImpl.abortPluginProcessByShizuku(pidSaved)
    }

    // Create
    // Update
    // Read
    suspend fun getPluginExecuteStatusList(): List<PluginExecuteStatus> {
        val pluginExecuteStatusEntityList = pluginExecutionDao.listPluginExecutions()
        val pluginExecuteStatusList = pluginExecuteStatusEntityList
            .map { pluginExecuteStatusEntry ->
                pluginExecuteStatusEntry.toPluginExecuteStatus()
            }
        return pluginExecuteStatusList
    }

    // Delete
    suspend fun deleteExecuteRecordByPluginID(pluginID: String) {
        pluginExecutionDao.deletePluginExecutionByPluginId(pluginID)
    }

    suspend fun deleteAllExecuteRecord() {
        val pluginExecutionDao = rootlessStoreDatabase.pluginExecutionDao()
        pluginExecutionDao.deleteAllPluginExecutions()
    }
}
