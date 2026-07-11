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

    private val pluginInfoDAO = rootlessStoreDatabase.pluginInfoDao()

    // Create
    override suspend fun insertOnePluginInfo(
        pluginManifestLocal: PluginManifestLocal
    ){
        val pluginInfoEntity = pluginManifestLocal.toPluginInfoEntity()
        pluginInfoDAO.insertOnePluginInfo(pluginInfoEntity)
    }
    suspend fun insertOnePluginInfo(
        pluginManifestRemote: PluginManifestRemote
    ){
        val pluginInfoEntity = pluginManifestRemote.toPluginInfoEntity()
        pluginInfoDAO.insertOnePluginInfo(pluginInfoEntity)
    }

    // Update
    override suspend fun enablePluginByID(pluginID: String) {
        pluginInfoDAO.updateEnabled(pluginID = pluginID, enabled = true)
    }

    override suspend fun disablePluginByID(pluginID: String) {
        pluginInfoDAO.updateEnabled(pluginID = pluginID, enabled = false)
    }

    override suspend fun disableAllPlugin() {
        pluginInfoDAO.disableAllPlugin()
    }

    // READ
    override suspend fun getOnePluginInfo(
        pluginID: String
    ): PluginManifestRoom? {
        val pluginInfo = pluginInfoDAO.getOneEntirePluginInfoByPluginID(pluginID)
        return pluginInfo
    }

    override fun getWholePluginInfo(): Flow<List<PluginManifestRoom>> {
        val pluginManifestRoomList = pluginInfoDAO.getEntirePluginManifest()

        return pluginManifestRoomList
    }

    override fun getPluginInfoCount(): Flow<Int> {
        return pluginInfoDAO.getPluginInfoCount()
    }

    override suspend fun getTotalPluginCount(): Int {
        return pluginInfoDAO.getTotalPluginCount()
    }

    override suspend fun getEnabledPluginCount(): Int {
        return pluginInfoDAO.getEnabledPluginCount()
    }

    // Delete
    override suspend fun deleteOnePluginInfoByID(pluginID: String) {
        pluginInfoDAO.deleteOnePluginInfoByID(pluginID)
    }

}
