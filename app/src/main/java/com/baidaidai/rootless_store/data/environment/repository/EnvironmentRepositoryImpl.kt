package com.baidaidai.rootless_store.data.environment.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentEntity
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EnvironmentRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
    private val environmentGatewayImpl: EnvironmentGatewayImpl
) {
    private val environmentDao = rootlessStoreDatabase.environmentDao()

    // Create
    suspend fun insertEnvironment(
        environmentManifestLocal: EnvironmentManifestLocal
    ) {
        val environmentEntity = environmentManifestLocal.toEnvironmentEntity()
        environmentDao.insertEnvironment(environmentEntity)
    }
    suspend fun insertEnvironment(
        environmentManifestRemote: EnvironmentManifestRemote
    ) {
        val environmentEntity = environmentManifestRemote.toEnvironmentEntity()
        environmentDao.insertEnvironment(environmentEntity)
    }

    // Update
    suspend fun enableEnvironmentById(environmentId: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = true)
    }

    suspend fun disableEnvironmentById(environmentId: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = false)
    }

    // Read
    suspend fun findEnvironmentById(
        environmentId: String
    ): EnvironmentManifestRoom? {
        val environmentInfoRoom = environmentDao.findEnvironmentById(environmentId)
        return environmentInfoRoom
    }

    fun observeEnvironments(): Flow<List<EnvironmentManifestRoom>> {
        val environmentManifestList = environmentDao.observeEnvironments()
        return environmentManifestList
    }

    suspend fun resolveEnvironmentPath(): String {
        return environmentDao.observeEnabledEnvironments()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentRuntimePath(environmentManifest)
            }
    }

    suspend fun resolveEnvironmentLdPath(): String {
        return environmentDao.observeEnabledEnvironments()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.resolveEnvironmentLdPath(environmentManifest)
            }
    }

    suspend fun resolveEnvironmentConfig(): Map<String, String> {
        val environmentManifests = environmentDao.observeEnabledEnvironments().first()

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
