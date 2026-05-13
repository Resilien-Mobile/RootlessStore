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
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult
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
            val sourceAuthenticationResult = pluginSourceGatewayImpl.getPluginSourceAuthenticationResult(pluginSourceAuthFormInput)

            /**
             * 验证，打断异常会话
             */
            return when(sourceAuthenticationResult){
                is PluginSourceAuthenticationResult.Success -> {
                    val pluginSourceEntity = PluginSourceEntity
                        .fromPluginSourceLocal(pluginSource)
                        .copy(userAccessToken = sourceAuthenticationResult.userAccessToken)

                    pluginSourceDAO.insertOnePluginSource(pluginSourceEntity)

                    PluginSourceEvent.Success
                }
                is PluginSourceAuthenticationResult.AccessDenied -> {
                    PluginSourceEvent.SourceError(
                        errorMessage = "Verification failed",
                        errorCause = sourceAuthenticationResult.errorMessage
                    )
                }
                is PluginSourceAuthenticationResult.ServerError -> {
                    PluginSourceEvent.SourceError(
                        errorMessage = "Server error",
                        errorCause = "Please try again later"
                    )
                }
                is PluginSourceAuthenticationResult.NetworkError -> {
                    PluginSourceEvent.SourceError(
                        errorMessage = "Network error",
                        errorCause = "Please try again later"
                    )
                }
                else -> {
                    PluginSourceEvent.SourceError(
                        errorMessage = "不可能的错误",
                        errorCause = "你猜是什么错误呢？"
                    )
                }
            }

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
