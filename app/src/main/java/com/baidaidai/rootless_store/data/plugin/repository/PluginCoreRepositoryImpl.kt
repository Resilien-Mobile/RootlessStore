package com.baidaidai.rootless_store.data.plugin.repository

import android.net.Uri
import android.util.Log
import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.plugin.gateway.PluginCoreGatewayImpl
import com.baidaidai.rootless_store.data.plugin.room.EnvironmentInfoEntity
import com.baidaidai.rootless_store.data.plugin.room.PluginInfoEntity
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.LocalManifest
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.repository.PluginCoreRepository
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestLocal
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.plugin.manifest.RootlessStoreManifestCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PluginCoreRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
    private val pluginCoreGatewayImpl: PluginCoreGatewayImpl
): PluginCoreRepository {

    private val pluginInfoDAO = rootlessStoreDatabase.pluginInfoDao()
    private val environmentInfoDAO = rootlessStoreDatabase.environmentInfoDao()

    // Create
    override suspend fun insertOnePluginInfo(
        pluginInfoEntity: PluginInfoEntity
    ){
        pluginInfoDAO.insertOnePluginInfo(pluginInfoEntity)
    }
    override suspend fun insertOneEnvironmentInfo(
        environmentInfoEntity: EnvironmentInfoEntity
    ){
        environmentInfoDAO.insertOneEnvironmentInfo(environmentInfoEntity)
    }

    // Update
    override suspend fun enablePluginByID(pluginID: String) {
        pluginInfoDAO.updateEnabled(pluginID = pluginID, enabled = true)
    }

    override suspend fun disablePluginByID(pluginID: String) {
        pluginInfoDAO.updateEnabled(pluginID = pluginID, enabled = false)
    }

    override suspend fun enableEnvironmentByID(environmentID: String) {
        environmentInfoDAO.updateEnabled(environmentID = environmentID, enabled = true)
    }

    override suspend fun disableEnvironmentByID(environmentID: String) {
        environmentInfoDAO.updateEnabled(environmentID = environmentID, enabled = false)
    }

    // READ
    override suspend fun getOnePluginInfo(
        pluginID: String
    ): PluginManifestLocal? {
        val pluginInfo = pluginInfoDAO.getOneEntirePluginInfoByPluginID(pluginID)
        return pluginInfo
    }

    override suspend fun getOneEnvironmentInfo(
        environmentID: String
    ): EnvironmentManifestLocal? {
        val environmentInfo = environmentInfoDAO.getOneEntireEnvironmentInfoByEnvironmentID(environmentID)
        return environmentInfo
    }

    override fun getWholePluginInfo(): Flow<List<PluginManifestRoom>?> {
        val pluginManifestList = pluginInfoDAO.getEntirePluginManifest()
        return pluginManifestList
    }

    override fun getWholeEnvironmentInfo(): Flow<List<EnvironmentManifestRoom>?> {
        val environmentManifestList = environmentInfoDAO.getEntireEnvironmentManifest()
        return environmentManifestList
    }

    override fun getPluginInfoCount(): Flow<Int> {
        return pluginInfoDAO.getPluginInfoCount()
    }

    override suspend fun getTotalPluginCount(): Int {
        return pluginInfoDAO.getTotalPluginCount()
    }

    override suspend fun getEnabledPluginCount(): Int {
        return pluginInfoDAO.getEnabledPluginCount()
    }

    override suspend fun getAvailableEnvironmentPath(): String {
        return environmentInfoDAO.getEnabledEnvironment()
            .first()
            .joinToString(":") { environmentManifest ->
                pluginCoreGatewayImpl.getEnvironmentRuntimePATH(environmentManifest)
            }
    }

    override suspend fun getAvailableEnvironmentLDPATH(): String {
        return environmentInfoDAO.getEnabledEnvironment()
            .first()
            .joinToString(":") { environmentManifest ->
                pluginCoreGatewayImpl.getEnvironmentLDPATH(environmentManifest)
            }
    }

    override suspend fun getAvailableEnvironmentConfig(): Map<String, String> {
        val environmentManifests = environmentInfoDAO.getEnabledEnvironment().first()

        return buildMap {
            environmentManifests.forEach { environmentManifest ->
                putAll(pluginCoreGatewayImpl.getEnvironmentConfig(environmentManifest))
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
    override suspend fun deleteOnePluginInfo(pluginInfoEntity: PluginInfoEntity) {
        pluginInfoDAO.deleteOnePluginInfo(pluginInfoEntity)
    }

    suspend fun deleteOneEnvironmentInfo(environmentEntity: EnvironmentInfoEntity) {
        environmentInfoDAO.deleteOneEnvironmentInfo(environmentEntity)
    }

    // Operator
    override suspend fun installOnePlugin(
        uri: Uri,
    ): PluginError?{

        val pluginType = pluginCoreGatewayImpl.judgeManifest(uri)

        when(pluginType){
            LocalManifest.PluginManifestLocal -> {

                Log.d("PluginCoreRepositoryImpl.installOnePlugin","pluginType: ${pluginType.name}")

                try {
                    val pluginManiFestRoom = pluginCoreGatewayImpl.parsePluginManifest(uri).toManifestRoom()
                    val pluginInfoEntity = PluginInfoEntity.fromPluginManifestRoom(pluginManiFestRoom)

                    pluginCoreGatewayImpl.installPluginFromLocal(uri)
                    pluginCoreGatewayImpl.setPluginEntryPointExecutable(pluginManiFestRoom)
                    insertOnePluginInfo(pluginInfoEntity)

                    return null
                }catch (error: Throwable){
                    val errorStack  = error.stackTrace.OutOfStringLike()

                    return PluginError(
                        errorMessage = error.message!!,
                        errorCause = errorStack
                    )
                }
            }

            LocalManifest.EnvironmentManifestLocal -> {

                Log.d("PluginCoreRepositoryImpl.installOnePlugin","pluginType: ${pluginType.name}")

                try {
                    val environmentManiFest = pluginCoreGatewayImpl.parseEnvironmentManifest(uri).toManifestRoom()
                    val environmentEntity = EnvironmentInfoEntity.fromEnvironmentManifestRoom(environmentManiFest)

                    pluginCoreGatewayImpl.installEnvironmentFromLocal(uri)
                    pluginCoreGatewayImpl.setEnvironmentEntryPointExecutable(environmentManiFest)
                    insertOneEnvironmentInfo(environmentEntity)

                    return null
                }catch (error: Throwable){
                    val errorStack  = error.stackTrace.OutOfStringLike()

                    return PluginError(
                        errorMessage = error.message!!,
                        errorCause = errorStack
                    )
                }
            }
        }
    }

    override suspend fun installOnePluginFromMarket(
        pluginURI: String,
        manifest: RootlessStoreManifestCollection
    ): PluginError? {
        try {
            when(manifest){
                is PluginManifest -> {
                    val pluginManifestRemote = manifest as PluginManifestRemote
                    val pluginManifestRoom = pluginManifestRemote.toManifestRoom()

                    val pluginInfoEntity = PluginInfoEntity.fromPluginManifestRoom(pluginManifestRoom)

                    pluginCoreGatewayImpl.installPluginFromMarket(pluginURI,pluginManifestRemote)
                    pluginCoreGatewayImpl.setPluginEntryPointExecutable(pluginManifestRoom = pluginManifestRemote.toManifestRoom())
                    insertOnePluginInfo(pluginInfoEntity = pluginInfoEntity)

                    return null
                }
                is EnvironmentManifest -> {
                    val environmentManifestRemote = manifest as EnvironmentManifestRemote
                    val environmentManifestRoom = environmentManifestRemote.toEnvironmentManifestRoom()

                    val environmentInfoEntity = EnvironmentInfoEntity.fromEnvironmentManifestRoom(environmentManifestRoom)

                    pluginCoreGatewayImpl.installEnvironmentFromMarket(pluginURI, environmentManifestRemote)
                    pluginCoreGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifestRoom = environmentManifestRoom)
                    insertOneEnvironmentInfo(environmentInfoEntity = environmentInfoEntity)

                    return null
                }
            }
        }catch (error: Throwable){
            val errorStack  = error.stackTrace.OutOfStringLike()

            return PluginError(
                errorMessage = error.message!!,
                errorCause = errorStack
            )
        }
    }

}