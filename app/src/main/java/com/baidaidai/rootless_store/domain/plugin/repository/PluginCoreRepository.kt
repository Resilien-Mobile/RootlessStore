package com.baidaidai.rootless_store.domain.plugin.repository

import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow

interface PluginCoreRepository {
    // Create
    suspend fun insertPlugin(pluginManifestLocal: PluginManifestLocal)

    // Read
    suspend fun findPlugin(pluginId: String): PluginManifestRoom?
    fun observePlugins(): Flow<List<PluginManifestRoom>?>
    fun observePluginCount(): Flow<Int>
    suspend fun getTotalPluginCount(): Int
    suspend fun getEnabledPluginCount(): Int

    // Update
    suspend fun enablePlugin(pluginId: String)
    suspend fun disablePlugin(pluginId: String)
    suspend fun disableAllPlugins()

    // Delete
    suspend fun deletePluginById(pluginId: String)
}
