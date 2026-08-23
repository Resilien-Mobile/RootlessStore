package com.baidaidai.rootless_store.domain.source.repository

import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import kotlinx.coroutines.flow.Flow

interface PluginSourceRepository {
    // Add
    suspend fun addPluginSource(sourceEndpointInput: PluginSourceEndpointInput): PluginSourceEvent

    suspend fun addAuthenticatedPluginSource(authenticationInput: PluginSourceAuthenticationInput): PluginSourceEvent

    // Update
    suspend fun updatePluginSource(
        sourceId: String,
        sourceName: String,
        sourceRemoteEndpoint: String
    )

    // Read
    suspend fun findPluginSource(sourceId: String): PluginSource?

    fun observePluginSources(): Flow<List<PluginSource>>

    fun observePluginSourceCount(): Flow<Int>

    // Delete
    suspend fun deletePluginSource(pluginSource: PluginSource)
}
