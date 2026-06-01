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
        val pluginManifestRoomListFlow = pluginRepositoryImpl.getWholePluginInfo()

        return pluginManifestRoomListFlow.map { pluginManifestRoomList ->
            pluginManifestRoomList.map { pluginManifestRoom ->

                // If user provide icon uri
                if (pluginManifestRoom.iconURI != null){
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