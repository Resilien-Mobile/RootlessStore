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
    suspend fun enableEnvironmentByID(environmentID: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentID, isEnabled = true)
    }

    suspend fun disableEnvironmentByID(environmentID: String) {
        environmentDao.updateEnvironmentEnabled(environmentId = environmentID, isEnabled = false)
    }

    // Read
    suspend fun findEnvironmentInfoRoomByID(
        environmentID: String
    ): EnvironmentManifestRoom? {
        val environmentInfoRoom = environmentDao.findEnvironmentById(environmentID)
        return environmentInfoRoom
    }

    fun getWholeEnvironmentInfoRoom(): Flow<List<EnvironmentManifestRoom>> {
        val environmentManifestList = environmentDao.observeEnvironments()
        return environmentManifestList
    }

    suspend fun getAvailableEnvironmentPath(): String {
        return environmentDao.observeEnabledEnvironments()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.getEnvironmentRuntimePATH(environmentManifest)
            }
    }

    suspend fun getAvailableEnvironmentLDPATH(): String {
        return environmentDao.observeEnabledEnvironments()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.getEnvironmentLDPATH(environmentManifest)
            }
    }

    suspend fun getAvailableEnvironmentConfig(): Map<String, String> {
        val environmentManifests = environmentDao.observeEnabledEnvironments().first()

        return buildMap {
            environmentManifests.forEach { environmentManifest ->
                putAll(environmentGatewayImpl.getEnvironmentConfig(environmentManifest))
            }
        }
    }

    suspend fun getEnvironmentConfigKeyList(): List<String> {
        return getAvailableEnvironmentConfig().keys.toList()
    }

    suspend fun getEnvironmentConfigValueList(): List<String> {
        return getAvailableEnvironmentConfig().values.toList()
    }

    // Delete
    suspend fun deleteEnvironmentInfoByID(environmentID: String) {
        environmentDao.deleteEnvironmentById(environmentID)
    }
}
