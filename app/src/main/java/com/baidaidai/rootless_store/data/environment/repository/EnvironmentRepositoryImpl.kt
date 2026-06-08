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
    private val environmentInfoDAO = rootlessStoreDatabase.environmentInfoDao()

    // Create
    suspend fun insertOneEnvironmentInfo(
        environmentManifestLocal: EnvironmentManifestLocal
    ) {
        val environmentInfoEntity = environmentManifestLocal.toEnvironmentInfoEntity()
        environmentInfoDAO.insertOneEnvironmentInfo(environmentInfoEntity)
    }
    suspend fun insertOneEnvironmentInfo(
        environmentManifestRemote: EnvironmentManifestRemote
    ) {
        val environmentInfoEntity = environmentManifestRemote.toEnvironmentInfoEntity()
        environmentInfoDAO.insertOneEnvironmentInfo(environmentInfoEntity)
    }

    // Update
    suspend fun enableEnvironmentByID(environmentID: String) {
        environmentInfoDAO.updateEnabled(environmentID = environmentID, enabled = true)
    }

    suspend fun disableEnvironmentByID(environmentID: String) {
        environmentInfoDAO.updateEnabled(environmentID = environmentID, enabled = false)
    }

    // Read
    suspend fun getOneEnvironmentInfoRoomByID(
        environmentID: String
    ): EnvironmentManifestRoom? {
        val environmentInfoRoom = environmentInfoDAO.getOneEntireEnvironmentInfoByEnvironmentID(environmentID)
        return environmentInfoRoom
    }

    fun getWholeEnvironmentInfoRoom(): Flow<List<EnvironmentManifestRoom>> {
        val environmentManifestList = environmentInfoDAO.getEntireEnvironmentManifest()
        return environmentManifestList
    }

    suspend fun getAvailableEnvironmentPath(): String {
        return environmentInfoDAO.getEnabledEnvironment()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.getEnvironmentRuntimePATH(environmentManifest)
            }
    }

    suspend fun getAvailableEnvironmentLDPATH(): String {
        return environmentInfoDAO.getEnabledEnvironment()
            .first()
            .joinToString(":") { environmentManifest ->
                environmentGatewayImpl.getEnvironmentLDPATH(environmentManifest)
            }
    }

    suspend fun getAvailableEnvironmentConfig(): Map<String, String> {
        val environmentManifests = environmentInfoDAO.getEnabledEnvironment().first()

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
    suspend fun deleteOneEnvironmentInfoByID(environmentID: String) {
        environmentInfoDAO.deleteOneEnvironmentInfoByID(environmentID)
    }
}
