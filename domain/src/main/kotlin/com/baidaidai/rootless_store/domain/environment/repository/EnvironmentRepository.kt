package com.baidaidai.rootless_store.domain.environment.repository

import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import kotlinx.coroutines.flow.Flow

interface EnvironmentRepository {

    suspend fun addEnvironment(
        environmentManifest: EnvironmentManifest
    )

    suspend fun findEnvironmentById(
        environmentId: String
    ): EnvironmentManifest?

    fun observeEnvironments(): Flow<List<EnvironmentManifest>>

    suspend fun resolveEnvironmentRuntimePath(): String

    suspend fun resolveEnvironmentLdPath(): String

    suspend fun resolveEnvironmentConfig(): Map<String, String>

    suspend fun resolveEnvironmentConfigKeys(): List<String>

    suspend fun resolveEnvironmentConfigValues(): List<String>

    suspend fun deleteEnvironmentById(
        environmentId: String
    )

}
