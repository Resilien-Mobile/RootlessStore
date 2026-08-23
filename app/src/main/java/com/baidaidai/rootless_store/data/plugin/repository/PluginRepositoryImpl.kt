package com.baidaidai.rootless_store.data.plugin.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginEntity
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
    override suspend fun insertPlugin(
        pluginManifestLocal: PluginManifestLocal
    ){
        val pluginEntity = pluginManifestLocal.toPluginEntity()
        pluginDao.insertPlugin(pluginEntity)
    }
    suspend fun insertPlugin(
        pluginManifestRemote: PluginManifestRemote
    ){
        val pluginEntity = pluginManifestRemote.toPluginEntity()
        pluginDao.insertPlugin(pluginEntity)
    }

    // Update
    override suspend fun enablePlugin(pluginId: String) {
        pluginDao.updatePluginEnabled(pluginId = pluginId, isEnabled = true)
    }

    override suspend fun disablePlugin(pluginId: String) {
        pluginDao.updatePluginEnabled(pluginId = pluginId, isEnabled = false)
    }

    override suspend fun disableAllPlugins() {
        pluginDao.disableAllPlugins()
    }

    // READ
    override suspend fun findPlugin(
        pluginId: String
    ): PluginManifestRoom? {
        val pluginManifest = pluginDao.findPluginById(pluginId)
        return pluginManifest
    }

    override fun observePlugins(): Flow<List<PluginManifestRoom>> {
        val pluginManifestRoomList = pluginDao.observePlugins()

        return pluginManifestRoomList
    }

    override fun observePluginCount(): Flow<Int> {
        return pluginDao.observePluginCount()
    }

    override suspend fun getTotalPluginCount(): Int {
        return pluginDao.getPluginCount()
    }

    override suspend fun getEnabledPluginCount(): Int {
        return pluginDao.getEnabledPluginCount()
    }

    // Delete
    override suspend fun deletePluginById(pluginId: String) {
        pluginDao.deletePluginById(pluginId)
    }

}
