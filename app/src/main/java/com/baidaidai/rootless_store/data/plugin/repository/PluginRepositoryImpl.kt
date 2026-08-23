package com.baidaidai.rootless_store.data.plugin.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginInfoEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.repository.PluginCoreRepository
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PluginRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
): PluginCoreRepository {

    private val pluginDao = rootlessStoreDatabase.pluginDao()

    // Create
    override suspend fun insertPluginInfo(
        pluginManifestLocal: PluginManifestLocal
    ){
        val pluginInfoEntity = pluginManifestLocal.toPluginInfoEntity()
        pluginDao.insertPlugin(pluginInfoEntity)
    }
    suspend fun insertPluginInfo(
        pluginManifestRemote: PluginManifestRemote
    ){
        val pluginInfoEntity = pluginManifestRemote.toPluginInfoEntity()
        pluginDao.insertPlugin(pluginInfoEntity)
    }

    // Update
    override suspend fun enablePluginByID(pluginID: String) {
        pluginDao.updatePluginEnabled(pluginId = pluginID, isEnabled = true)
    }

    override suspend fun disablePluginByID(pluginID: String) {
        pluginDao.updatePluginEnabled(pluginId = pluginID, isEnabled = false)
    }

    override suspend fun disableAllPlugin() {
        pluginDao.disableAllPlugins()
    }

    // READ
    override suspend fun findPluginInfo(
        pluginID: String
    ): PluginManifestRoom? {
        val pluginInfo = pluginDao.findPluginById(pluginID)
        return pluginInfo
    }

    override fun getWholePluginInfo(): Flow<List<PluginManifestRoom>> {
        val pluginManifestRoomList = pluginDao.observePlugins()

        return pluginManifestRoomList
    }

    override fun getPluginInfoCount(): Flow<Int> {
        return pluginDao.observePluginCount()
    }

    override suspend fun getTotalPluginCount(): Int {
        return pluginDao.getPluginCount()
    }

    override suspend fun getEnabledPluginCount(): Int {
        return pluginDao.getEnabledPluginCount()
    }

    // Delete
    override suspend fun deletePluginInfoByID(pluginID: String) {
        pluginDao.deletePluginById(pluginID)
    }

}
