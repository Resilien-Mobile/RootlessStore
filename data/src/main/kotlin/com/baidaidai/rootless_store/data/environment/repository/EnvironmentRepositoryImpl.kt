package com.baidaidai.rootless_store.data.environment.repository

import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentRepository
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
): EnvironmentRepository {
    private val environmentDao = rootlessStoreDatabase.environmentDao()

    // Add
    override suspend fun addEnvironment(environmentManifest: EnvironmentManifest) {
        environmentDao.insertEnvironment(environmentManifest.toEnvironmentEntity())
    }

    // Read
    override suspend fun findEnvironmentById(environmentId: String): EnvironmentManifest? {
        return environmentDao.findEnvironmentById(environmentId)?.toEnvironmentManifest()
    }

    override fun observeEnvironments(): Flow<List<EnvironmentManifest>> {
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

    override suspend fun resolveEnvironmentRuntimePath(): String {
        return resolveEnabledEnvironmentManifests()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentRuntimePath(environmentManifest)
            }
    }

    override suspend fun resolveEnvironmentLdPath(): String {
        return resolveEnabledEnvironmentManifests()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentLdPath(environmentManifest)
            }
    }

    override suspend fun resolveEnvironmentConfig(): Map<String, String> {
        return buildMap {
            resolveEnabledEnvironmentManifests().forEach { environmentManifest ->
                putAll(environmentGatewayImpl.resolveEnvironmentConfig(environmentManifest))
            }
        }
    }

    override suspend fun resolveEnvironmentConfigKeys(): List<String> {
        return resolveEnvironmentConfig().keys.toList()
    }

    override suspend fun resolveEnvironmentConfigValues(): List<String> {
        return resolveEnvironmentConfig().values.toList()
    }

    // Delete
    override suspend fun deleteEnvironmentById(environmentId: String) {
        environmentDao.deleteEnvironmentById(environmentId)
    }
}
