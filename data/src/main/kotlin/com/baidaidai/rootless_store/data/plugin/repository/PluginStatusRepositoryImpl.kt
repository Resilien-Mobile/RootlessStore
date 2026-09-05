package com.baidaidai.rootless_store.data.plugin.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.plugin.database.PluginStatusEntity
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginStatus
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.plugin.model.PluginStatus
import com.baidaidai.rootless_store.domain.plugin.repository.PluginStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PluginStatusRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
) : PluginStatusRepository {

    private val pluginStatusDao = rootlessStoreDatabase.pluginStatusDao()

    // Add
    override suspend fun registerPluginStatus(pluginId: String, origin: PluginOrigin) {
        pluginStatusDao.insertPluginStatus(
            PluginStatusEntity(
                pluginId = pluginId,
                isEnabled = false,
                state = PluginState.Great,
                origin = origin
            )
        )
    }

    // Read
    override suspend fun findPluginStatus(pluginId: String): PluginStatus? {
        return pluginStatusDao.findPluginStatusById(pluginId)?.toPluginStatus()
    }

    override fun observePluginStatus(pluginId: String): Flow<PluginStatus?> {
        return pluginStatusDao.observePluginStatusById(pluginId).map { it?.toPluginStatus() }
    }

    override fun observePluginStatuses(): Flow<List<PluginStatus>> {
        return pluginStatusDao.observePluginStatuses().map { entities ->
            entities.map { it.toPluginStatus() }
        }
    }

    override suspend fun getEnabledPluginCount(): Int {
        return pluginStatusDao.getEnabledPluginCount()
    }

    // Update
    override suspend fun enablePlugin(pluginId: String) {
        pluginStatusDao.updatePluginEnabled(pluginId = pluginId, isEnabled = true)
    }

    override suspend fun disablePlugin(pluginId: String) {
        pluginStatusDao.updatePluginEnabled(pluginId = pluginId, isEnabled = false)
    }

    override suspend fun disableAllPlugins() {
        pluginStatusDao.updateAllPluginsEnabled(isEnabled = false)
    }

    // Delete
    override suspend fun deletePluginStatus(pluginId: String) {
        pluginStatusDao.deletePluginStatusById(pluginId)
    }
}
