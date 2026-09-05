package com.baidaidai.rootless_store.domain.plugin.repository

import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginStatus
import kotlinx.coroutines.flow.Flow

interface PluginStatusRepository {
    // Add
    suspend fun registerPluginStatus(pluginId: String, origin: PluginOrigin)

    // Read
    suspend fun findPluginStatus(pluginId: String): PluginStatus?
    fun observePluginStatus(pluginId: String): Flow<PluginStatus?>
    fun observePluginStatuses(): Flow<List<PluginStatus>>
    suspend fun getEnabledPluginCount(): Int

    // Update
    suspend fun enablePlugin(pluginId: String)
    suspend fun disablePlugin(pluginId: String)
    suspend fun disableAllPlugins()

    // Delete
    suspend fun deletePluginStatus(pluginId: String)
}
