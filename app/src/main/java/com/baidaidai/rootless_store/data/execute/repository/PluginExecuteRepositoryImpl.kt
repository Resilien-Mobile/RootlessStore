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

    private val pluginExecuteStatusDao = rootlessStoreDatabase.pluginExecuteStatusDao()

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

    // Create
    // Update
    // Read
    suspend fun getPluginExecuteStatusList(): List<PluginExecuteStatus> {
        val pluginExecuteStatusEntityList = pluginExecuteStatusDao.getAllExecutingPluginEntity()
        val pluginExecuteStatusList = pluginExecuteStatusEntityList
            .map { pluginExecuteStatusEntry ->
                pluginExecuteStatusEntry.toPluginExecuteStatus()
            }
        return pluginExecuteStatusList
    }

    // Delete
    suspend fun deleteExecuteRecordByPluginID(pluginID: String) {
        pluginExecuteStatusDao.deleteExecuteRecordByPluginID(pluginID)
    }

    suspend fun deleteAllExecuteRecord() {
        val pluginExecuteStatusDao = rootlessStoreDatabase.pluginExecuteStatusDao()
        pluginExecuteStatusDao.deleteAllExecuteRecord()
    }
}
