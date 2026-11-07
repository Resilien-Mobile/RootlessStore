package com.baidaidai.rootless_store.domain.environment.repository

import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import kotlinx.coroutines.flow.Flow

interface EnvironmentStatusRepository {

    suspend fun registerEnvironmentStatus(
        environmentId: String,
        origin: PluginOrigin
    )

    suspend fun findEnvironmentStatus(
        environmentId: String
    ): EnvironmentStatus?

    fun observeEnvironmentStatus(
        environmentId: String
    ): Flow<EnvironmentStatus?>

    fun observeEnvironmentStatuses(): Flow<List<EnvironmentStatus>>

    fun observeEnabledEnvironmentStatuses(): Flow<List<EnvironmentStatus>>

    suspend fun getEnabledEnvironmentCount(): Int

    suspend fun enableEnvironment(
        environmentId: String
    )

    suspend fun disableEnvironment(
        environmentId: String
    )

    suspend fun deleteEnvironmentStatus(
        environmentId: String
    )

}
