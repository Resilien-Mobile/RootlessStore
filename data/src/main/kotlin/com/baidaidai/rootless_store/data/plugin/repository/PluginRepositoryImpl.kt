package com.baidaidai.rootless_store.data.plugin.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginEntity
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.repository.PluginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PluginRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
) : PluginRepository {

    private val pluginDao = rootlessStoreDatabase.pluginDao()

    // Add
    override suspend fun addPlugin(pluginManifest: PluginManifest) {
        pluginDao.insertPlugin(pluginManifest.toPluginEntity())
    }

    // Read
    override suspend fun findPlugin(pluginId: String): PluginManifest? {
        return pluginDao.findPluginById(pluginId)?.toPluginManifest()
    }

    override fun observePlugins(): Flow<List<PluginManifest>> {
        return pluginDao.observePlugins().map { entities ->
            entities.map { it.toPluginManifest() }
        }
    }

    override fun observePluginCount(): Flow<Int> {
        return pluginDao.observePluginCount()
    }

    override suspend fun getPluginCount(): Int {
        return pluginDao.getPluginCount()
    }

    // Delete
    override suspend fun deletePluginById(pluginId: String) {
        pluginDao.deletePluginById(pluginId)
    }
}
