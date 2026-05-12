package com.baidaidai.rootless_store.data.source.repository

import android.content.Context
import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.gateway.PluginSourceGatewayImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import com.baidaidai.rootless_store.domain.source.repository.PluginSourceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import kotlinx.coroutines.flow.map

class PluginSourceRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    rootlessStoreDatabase: RootlessStoreDatabase,
    val pluginSourceGatewayImpl: PluginSourceGatewayImpl
): PluginSourceRepository {

    override val appDatabase = rootlessStoreDatabase

    private val pluginSourceDAO = appDatabase.pluginSourceDao()

    // Create
    override suspend fun insertOnePluginSourceByDefault(
        sourceEndpointInput: PluginSourceEndpointInput
    ): PluginSourceEvent {
        try{
            val pluginSource = pluginSourceGatewayImpl.getPluginSource(sourceEndpointInput.sourceRemoteEndpoint)
            val sourceAuthenticationInfo = pluginSource.pluginSourceAuthenticationMeta

            /**
             * 验证，拉起WebView
             */
            if (sourceAuthenticationInfo.requireAuthentication){
                return PluginSourceEvent.SourceAuthentication
            }


            val newPluginSourceEntity = PluginSourceEntity.fromPluginSourceLocal(pluginSource)

            pluginSourceDAO.insertOnePluginSource(newPluginSourceEntity)

            return PluginSourceEvent.Success

        }catch (error: Throwable){

            return PluginSourceEvent.SourceError(
                errorMessage = error.message.toString(),
                errorCause = error.stackTrace.OutOfStringLike()
            )

        }
    }

    override suspend fun insertOnePluginSourceByAuthentication(
        pluginSourceAuthFormInput: PluginSourceAuthFormInput
    ): PluginSourceEvent {
        try{

            val pluginSource = pluginSourceGatewayImpl.getPluginSource(sourceRemoteEndpoint = pluginSourceAuthFormInput.sourceRemoteEndpoint)
            val sourceAuthenticationInfo = pluginSourceGatewayImpl.getPluginSourceAuthenticationInfo(pluginSourceAuthFormInput)

            /**
             * 验证，打断异常会话
             */
            if (sourceAuthenticationInfo.userAccessToken == ""){
                return PluginSourceEvent.SourceError(
                    errorMessage = "Verification failed",
                    errorCause = ""
                )
            }


            val pluginSourceEntity = PluginSourceEntity
                .fromPluginSourceLocal(pluginSource)
                .copy(userAccessToken = sourceAuthenticationInfo.userAccessToken)

            pluginSourceDAO.insertOnePluginSource(pluginSourceEntity)

            return PluginSourceEvent.Success

        }catch (error: Throwable){

            return PluginSourceEvent.SourceError(
                errorMessage = error.message.toString(),
                errorCause = error.stackTrace.OutOfStringLike()
            )

        }
    }

    // Update
    override suspend fun updateOnePluginSource(
        sourceID: String,
        sourceName: String,
        sourceRemoteEndpoint: String
    ) {
        pluginSourceDAO.updateOnePluginSource(
            sourceID = sourceID,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint
        )
    }

    // Read
    override suspend fun getOnePluginSource(
        sourceID: String
    ): PluginSourceEntity? {
        return pluginSourceDAO.getOnePluginSourceBySourceID(sourceID)
    }

    override fun getAllPluginSources(): Flow<List<PluginSourceInfo>?> {
        val pluginSourceEntry = pluginSourceDAO.getAllPluginSources()

        val pluginSource = pluginSourceEntry.map { list ->
            list?.map { content ->
               content.toPluginSourceInfo()
            }
        }

        return pluginSource
    }

    override fun getPluginSourcesCount(): Flow<Int> {
        return pluginSourceDAO.getPluginSourcesCount()
    }

    // Delete
    override suspend fun deleteOnePluginSource(
        pluginSourceEntity: PluginSourceEntity
    ) {
        pluginSourceDAO.deleteOnePluginSource(pluginSourceEntity)
    }

}
