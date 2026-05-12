package com.baidaidai.rootless_store.domain.source.repository

import androidx.room.RoomDatabase
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import kotlinx.coroutines.flow.Flow

interface PluginSourceRepository {
    // 以DB为中心的Gateway

    val appDatabase: RoomDatabase

    // Create
    suspend fun insertOnePluginSourceByDefault(sourceEndpointInput: PluginSourceEndpointInput): PluginSourceEvent

    suspend fun insertOnePluginSourceByAuthentication(pluginSourceAuthFormInput: PluginSourceAuthFormInput): PluginSourceEvent

    // Update
    suspend fun updateOnePluginSource(
        sourceID: String,
        sourceName: String,
        sourceRemoteEndpoint: String
    )

    // Read
    suspend fun getOnePluginSource(sourceID: String): PluginSourceEntity?

    fun getAllPluginSources(): Flow<List<PluginSourceInfo>?>

    fun getPluginSourcesCount(): Flow<Int>

    // Delete
    suspend fun deleteOnePluginSource(pluginSourceEntity: PluginSourceEntity)
}
