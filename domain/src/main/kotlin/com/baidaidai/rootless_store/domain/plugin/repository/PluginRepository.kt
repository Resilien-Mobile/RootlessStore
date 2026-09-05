package com.baidaidai.rootless_store.domain.plugin.repository

import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import kotlinx.coroutines.flow.Flow

interface PluginRepository {
    // Add
    suspend fun addPlugin(pluginManifest: PluginManifest)

    // Read
    suspend fun findPlugin(pluginId: String): PluginManifest?
    fun observePlugins(): Flow<List<PluginManifest>>
    fun observePluginCount(): Flow<Int>
    suspend fun getPluginCount(): Int

    // Delete
    suspend fun deletePluginById(pluginId: String)
}
