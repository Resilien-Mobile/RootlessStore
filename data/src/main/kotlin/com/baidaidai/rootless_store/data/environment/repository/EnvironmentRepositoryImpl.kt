package com.baidaidai.rootless_store.data.environment.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentEntity
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentManifest
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EnvironmentRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
    private val environmentGatewayImpl: EnvironmentGatewayImpl,
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl
) {
    private val environmentDao = rootlessStoreDatabase.environmentDao()

    // Add
    suspend fun addEnvironment(environmentManifest: EnvironmentManifest) {
        environmentDao.insertEnvironment(environmentManifest.toEnvironmentEntity())
    }

    // Read
    suspend fun findEnvironmentById(environmentId: String): EnvironmentManifest? {
        return environmentDao.findEnvironmentById(environmentId)?.toEnvironmentManifest()
    }

    fun observeEnvironments(): Flow<List<EnvironmentManifest>> {
        return environmentDao.observeEnvironments().map { entities ->
            entities.map { it.toEnvironmentManifest() }
        }
    }

    private suspend fun resolveEnabledEnvironmentManifests(): List<EnvironmentManifest> {
        val enabledStatuses = environmentStatusRepositoryImpl.observeEnabledEnvironmentStatuses().first()
        return enabledStatuses.mapNotNull { status ->
            findEnvironmentById(status.environmentId)
        }
    }

    suspend fun resolveEnvironmentRuntimePath(): String {
        return resolveEnabledEnvironmentManifests()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentRuntimePath(environmentManifest)
            }
    }

    suspend fun resolveEnvironmentLdPath(): String {
        return resolveEnabledEnvironmentManifests()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentLdPath(environmentManifest)
            }
    }

    suspend fun resolveEnvironmentConfig(): Map<String, String> {
        return buildMap {
            resolveEnabledEnvironmentManifests().forEach { environmentManifest ->
                putAll(environmentGatewayImpl.resolveEnvironmentConfig(environmentManifest))
            }
        }
    }

    suspend fun resolveEnvironmentConfigKeys(): List<String> {
        return resolveEnvironmentConfig().keys.toList()
    }

    suspend fun resolveEnvironmentConfigValues(): List<String> {
        return resolveEnvironmentConfig().values.toList()
    }

    // Delete
    suspend fun deleteEnvironmentById(environmentId: String) {
        environmentDao.deleteEnvironmentById(environmentId)
    }
}
