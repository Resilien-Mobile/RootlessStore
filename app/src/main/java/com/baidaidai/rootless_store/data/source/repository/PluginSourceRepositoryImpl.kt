package com.baidaidai.rootless_store.data.source.repository

import android.content.Context
import com.baidaidai.rootless_store.core.util.OutOfStringLike
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.gateway.PluginSourceGatewayImpl
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEndpointInput
import com.baidaidai.rootless_store.domain.source.repository.PluginSourceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult
import kotlinx.coroutines.flow.map

class PluginSourceRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    rootlessStoreDatabase: RootlessStoreDatabase,
    val pluginSourceGatewayImpl: PluginSourceGatewayImpl
): PluginSourceRepository {

    override val appDatabase = rootlessStoreDatabase

    private val pluginSourceDao = appDatabase.pluginSourceDao()

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
                return PluginSourceEvent.SourceAuthentication
            }


            val newPluginSourceEntity = PluginSourceEntity.fromPluginSource(pluginSource)

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
        pluginSourceAuthFormInput: PluginSourceAuthFormInput
    ): PluginSourceEvent {
        try{

            val pluginSource = pluginSourceGatewayImpl.fetchPluginSource(sourceRemoteEndpoint = pluginSourceAuthFormInput.sourceRemoteEndpoint)
            val sourceAuthenticationResult = pluginSourceGatewayImpl.fetchPluginSourceAuthenticationResult(pluginSourceAuthFormInput)

            /**
             * 验证，打断异常会话
             */
            return when(sourceAuthenticationResult){
                is PluginSourceAuthenticationResult.Success -> {
                    val pluginSourceEntity = PluginSourceEntity
                        .fromPluginSource(pluginSource)
                        .copy(userAccessToken = sourceAuthenticationResult.userAccessToken)

                    pluginSourceDao.insertPluginSource(pluginSourceEntity)

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
    ): PluginSourceEntity? {
        return pluginSourceDao.findPluginSourceById(sourceId)
    }

    override fun observePluginSources(): Flow<List<PluginSource>?> {
        val pluginSourceEntry = pluginSourceDao.observePluginSources()

        val pluginSource = pluginSourceEntry.map { list ->
            list?.map { content ->
               content.toPluginSource()
            }
        }

        return pluginSource
    }

    override fun observePluginSourceCount(): Flow<Int> {
        return pluginSourceDao.observePluginSourceCount()
    }

    // Delete
    override suspend fun deletePluginSource(
        pluginSourceEntity: PluginSourceEntity
    ) {
        pluginSourceDao.deletePluginSource(pluginSourceEntity)
    }

}
