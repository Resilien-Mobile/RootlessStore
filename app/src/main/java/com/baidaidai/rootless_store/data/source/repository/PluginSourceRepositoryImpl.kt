package com.baidaidai.rootless_store.data.source.repository

import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.source.gateway.PluginSourceGatewayImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import com.baidaidai.rootless_store.domain.source.repository.PluginSourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSource
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceEntity
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult
import kotlinx.coroutines.flow.map

class PluginSourceRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase,
    private val pluginSourceGatewayImpl: PluginSourceGatewayImpl
): PluginSourceRepository {

    private val pluginSourceDao = rootlessStoreDatabase.pluginSourceDao()

    // Add
    override suspend fun addPluginSource(
        sourceEndpointInput: PluginSourceEndpointInput
    ): PluginSourceEvent {
        try{
            val pluginSource = pluginSourceGatewayImpl.fetchPluginSource(sourceEndpointInput.sourceRemoteEndpoint)
            val authenticationMetadata = pluginSource.pluginSourceAuthenticationMetadata

            /**
             * 验证，拉起WebView
             */
            if (authenticationMetadata.needsAuthentication){
                return PluginSourceEvent.AuthenticationRequired
            }


            val newPluginSourceEntity = pluginSource.toPluginSourceEntity()

            pluginSourceDao.insertPluginSource(newPluginSourceEntity)

            return PluginSourceEvent.Success

        }catch (error: Throwable){

            return PluginSourceEvent.SourceError(
                errorMessage = error.message.toString(),
                errorCause = error.stackTrace.OutOfStringLike()
            )

        }
    }

    override suspend fun addAuthenticatedPluginSource(
        authenticationInput: PluginSourceAuthenticationInput
    ): PluginSourceEvent {
        try{

            val pluginSource = pluginSourceGatewayImpl.fetchPluginSource(sourceRemoteEndpoint = authenticationInput.sourceRemoteEndpoint)
            val authenticationResult = pluginSourceGatewayImpl.fetchPluginSourceAuthenticationResult(authenticationInput)

            /**
             * 验证，打断异常会话
             */
            return when(authenticationResult){
                is PluginSourceAuthenticationResult.Success -> {
                    val pluginSourceEntity = pluginSource
                        .toPluginSourceEntity()
                        .copy(accessToken = authenticationResult.accessToken)

                    pluginSourceDao.insertPluginSource(pluginSourceEntity)

                    PluginSourceEvent.Success
                }
                is PluginSourceAuthenticationResult.AccessDenied -> {
                    PluginSourceEvent.SourceError(
                        errorMessage = "Verification failed",
                        errorCause = authenticationResult.errorMessage
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
    override suspend fun updatePluginSource(
        sourceId: String,
        sourceName: String,
        sourceRemoteEndpoint: String
    ) {
        pluginSourceDao.updatePluginSource(
            sourceId = sourceId,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint
        )
    }

    // Read
    override suspend fun findPluginSource(
        sourceId: String
    ): PluginSource? {
        return pluginSourceDao.findPluginSourceById(sourceId)?.toPluginSource()
    }

    override fun observePluginSources(): Flow<List<PluginSource>> {
        return pluginSourceDao.observePluginSources().map { pluginSourceEntities ->
            pluginSourceEntities.map { pluginSourceEntity ->
                pluginSourceEntity.toPluginSource()
            }
        }
    }

    override fun observePluginSourceCount(): Flow<Int> {
        return pluginSourceDao.observePluginSourceCount()
    }

    // Delete
    override suspend fun deletePluginSource(
        pluginSource: PluginSource
    ) {
        pluginSourceDao.deletePluginSource(pluginSource.toPluginSourceEntity())
    }

}
