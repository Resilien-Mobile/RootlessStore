package com.baidaidai.rootless_store.data.environment.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentInfoEntity
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
    suspend fun insertEnvironmentInfo(
        environmentManifestLocal: EnvironmentManifestLocal
    ) {
        val environmentInfoEntity = environmentManifestLocal.toEnvironmentInfoEntity()
        environmentDao.insertEnvironment(environmentInfoEntity)
    }
    suspend fun insertEnvironmentInfo(
        environmentManifestRemote: EnvironmentManifestRemote
    ) {
        val environmentInfoEntity = environmentManifestRemote.toEnvironmentInfoEntity()
        environmentDao.insertEnvironment(environmentInfoEntity)
    }

    // Update
    suspend fun enableEnvironmentById(environmentId: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = true)
    }

    suspend fun disableEnvironmentById(environmentId: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = false)
    }

    // Read
    suspend fun findEnvironmentInfoRoomById(
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
    suspend fun deleteEnvironmentInfoById(environmentId: String) {
        environmentDao.deleteEnvironmentById(environmentId)
    }
}
