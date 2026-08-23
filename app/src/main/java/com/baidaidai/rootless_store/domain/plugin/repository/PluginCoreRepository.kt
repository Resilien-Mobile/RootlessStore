package com.baidaidai.rootless_store.domain.plugin.repository

import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow

interface PluginCoreRepository {
    // Create
    suspend fun insertPluginInfo(pluginManifestLocal: PluginManifestLocal)

    // Read
    suspend fun findPluginInfo(pluginId: String): PluginManifestRoom?
    fun observePlugins(): Flow<List<PluginManifestRoom>?>
    fun observePluginCount(): Flow<Int>
    suspend fun getTotalPluginCount(): Int
    suspend fun getEnabledPluginCount(): Int

    // Update
    suspend fun enablePluginById(pluginId: String)
    suspend fun disablePluginById(pluginId: String)
    suspend fun disableAllPlugin()

    // Delete
    suspend fun deletePluginInfoById(pluginId: String)
}
