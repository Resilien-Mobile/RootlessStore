package com.baidaidai.rootless_store.data.execution.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.execution.database.PluginExecutionEntity
import com.baidaidai.rootless_store.data.execution.gateway.PluginExecutionGatewayImpl
import com.baidaidai.rootless_store.data.execution.mapper.PluginExecutionMapper.toPluginExecutionStatus
import com.baidaidai.rootless_store.domain.execution.model.PluginExecutionStatus
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
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
    suspend fun createPluginExecution(
        pluginManifestRoom: PluginManifestRoom,
        executionPid: Int,
        executionContext: ExecutionContext = ExecutionContext.LIMITED
    ) {
        val pluginExecutionEntity = PluginExecutionEntity
            .fromPluginManifest(pluginManifestRoom, executionPid)
            .copy(executionContext = executionContext)
        pluginExecutionDao.insertPluginExecution(pluginExecutionEntity)
    }

    // Update
    // Read
    suspend fun listPluginExecutionStatuses(): List<PluginExecutionStatus> {
        val pluginExecutionEntities = pluginExecutionDao.listPluginExecutions()
        val pluginExecutionStatuses = pluginExecutionEntities
            .map { pluginExecutionEntity ->
                pluginExecutionEntity.toPluginExecutionStatus()
            }
        return pluginExecutionStatuses
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
