package com.baidaidai.rootless_store.domain.source.repository

import androidx.room.RoomDatabase
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import kotlinx.coroutines.flow.Flow

interface PluginSourceRepository {
    // 以DB为中心的Gateway

    val appDatabase: RoomDatabase

    // Create
    suspend fun insertPluginSourceByDefault(sourceEndpointInput: PluginSourceEndpointInput): PluginSourceEvent

    suspend fun insertPluginSourceByAuthentication(pluginSourceAuthFormInput: PluginSourceAuthFormInput): PluginSourceEvent

    // Update
    suspend fun updatePluginSource(
        sourceId: String,
        sourceName: String,
        sourceRemoteEndpoint: String
    )

    // Read
    suspend fun findPluginSource(sourceId: String): PluginSourceEntity?

    fun observePluginSources(): Flow<List<PluginSource>?>

    fun observePluginSourceCount(): Flow<Int>

    // Delete
    suspend fun deletePluginSource(pluginSourceEntity: PluginSourceEntity)
}
