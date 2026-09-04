package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class ObservePluginsUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(): Flow<List<PluginManifestRoom>> {

        /**
         * This is merely a temporary solution for version 1.0.0
         * Starting from version 2.0.0
         * we will no longer use regex but instead rely directly on property checks
         */

        val uriRegex = Regex("^https?://",RegexOption.IGNORE_CASE)

        val pluginManifestRoomListFlow = pluginRepositoryImpl.observePlugins()

        return pluginManifestRoomListFlow.map { pluginManifestRoomList ->
            pluginManifestRoomList.map { pluginManifestRoom ->

                // If user provide icon uri
                if (pluginManifestRoom.iconUri != null && !uriRegex.containsMatchIn(
                        pluginManifestRoom.iconUri!!
                    )){
                    val pluginPackageDirectory = File(androidFileSystemCapabilityGatewayImpl.resolvePluginPackageDirectory(pluginManifestRoom))
                    val pluginIconUri = File(pluginPackageDirectory,pluginManifestRoom.iconUri).toURI()

                    pluginManifestRoom.copy(
                        iconUri = pluginIconUri.toString()
                    )
                }else{
                    pluginManifestRoom
                }

            }
        }
    }
}
