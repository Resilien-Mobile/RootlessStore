package com.baidaidai.rootless_store.data.environment.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentEntity
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EnvironmentRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
    private val environmentGatewayImpl: EnvironmentGatewayImpl
) {
    private val environmentDao = rootlessStoreDatabase.environmentDao()

    // Add
    suspend fun addEnvironment(
        environmentManifestLocal: EnvironmentManifestLocal
    ) {
        val environmentEntity = environmentManifestLocal.toEnvironmentEntity()
        environmentDao.insertEnvironment(environmentEntity)
    }
    suspend fun addEnvironment(
        environmentManifestRemote: EnvironmentManifestRemote
    ) {
        val environmentEntity = environmentManifestRemote.toEnvironmentEntity()
        environmentDao.insertEnvironment(environmentEntity)
    }

    // Update
    suspend fun enableEnvironment(environmentId: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = true)
    }

    suspend fun disableEnvironment(environmentId: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = false)
    }

    // Read
    suspend fun findEnvironmentById(
        environmentId: String
    ): EnvironmentManifestRoom? {
        return environmentDao.findEnvironmentById(environmentId)?.toEnvironmentManifestRoom()
    }

    fun observeEnvironments(): Flow<List<EnvironmentManifestRoom>> {
        return environmentDao.observeEnvironments().map { entities ->
            entities.map { it.toEnvironmentManifestRoom() }
        }
    }

    private fun observeEnabledEnvironmentManifests(): Flow<List<EnvironmentManifestRoom>> {
        return environmentDao.observeEnabledEnvironments().map { entities ->
            entities.map { it.toEnvironmentManifestRoom() }
        }
    }

    suspend fun resolveEnvironmentRuntimePath(): String {
        return observeEnabledEnvironmentManifests()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentRuntimePath(environmentManifest)
            }
    }

    suspend fun resolveEnvironmentLdPath(): String {
        return observeEnabledEnvironmentManifests()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentLdPath(environmentManifest)
            }
    }

    suspend fun resolveEnvironmentConfig(): Map<String, String> {
        val environmentManifests = observeEnabledEnvironmentManifests().first()

        return buildMap {
            environmentManifests.forEach { environmentManifest ->
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
