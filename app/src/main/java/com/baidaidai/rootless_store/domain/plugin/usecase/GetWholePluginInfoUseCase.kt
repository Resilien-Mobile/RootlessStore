package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class GetWholePluginInfoUseCase @Inject constructor(
    private val pluginRepositoryImpl: PluginCoreRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl
) {
    operator fun invoke(): Flow<List<PluginManifestRoom>> {

        /**
         * This is merely a temporary solution for version 1.0.0
         * Starting from version 2.0.0
         * we will no longer use regex but instead rely directly on property checks
         */

        val uriRegex = Regex("^https?://",RegexOption.IGNORE_CASE)

        val pluginManifestRoomListFlow = pluginRepositoryImpl.getWholePluginInfo()

        return pluginManifestRoomListFlow.map { pluginManifestRoomList ->
            pluginManifestRoomList.map { pluginManifestRoom ->

                // If user provide icon uri
                if (pluginManifestRoom.iconURI != null && !uriRegex.containsMatchIn(pluginManifestRoom.iconURI)){
                    val pluginPackageDirectory = File(androidFileSystemCapabilityGatewayImpl.getPluginPackageDirectory(pluginManifestRoom))
                    val pluginIconURI = File(pluginPackageDirectory,pluginManifestRoom.iconURI).toURI()

                    pluginManifestRoom.copy(
                        iconURI = pluginIconURI.toString()
                    )
                }else{
                    pluginManifestRoom
                }

            }
        }
    }
}