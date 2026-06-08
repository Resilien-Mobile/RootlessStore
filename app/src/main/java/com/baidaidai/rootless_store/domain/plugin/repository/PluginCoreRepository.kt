package com.baidaidai.rootless_store.domain.plugin.repository

import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow

interface PluginCoreRepository {
    // Create
    suspend fun insertOnePluginInfo(pluginManifestLocal: PluginManifestLocal)

    // Read
    suspend fun getOnePluginInfo(pluginID: String): PluginManifestRoom?
    fun getWholePluginInfo(): Flow<List<PluginManifestRoom>?>
    fun getPluginInfoCount(): Flow<Int>
    suspend fun getTotalPluginCount(): Int
    suspend fun getEnabledPluginCount(): Int

    // Update
    suspend fun enablePluginByID(pluginID: String)
    suspend fun disablePluginByID(pluginID: String)
    suspend fun disableAllPlugin()

    // Delete
    suspend fun deleteOnePluginInfoByID(pluginID: String)
}
