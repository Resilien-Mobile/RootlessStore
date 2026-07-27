package com.baidaidai.rootless_store.application.plugin

import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemDefaultOperatorGatewayImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import javax.inject.Inject

class GetPluginWebUiUriUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGatewayImpl: AndroidFileSystemDefaultOperatorGatewayImpl
) {
    operator fun invoke(
        pluginManifestRoom: PluginManifestRoom
    ): String {
        val pluginPackageDirectory = androidFileSystemDefaultOperatorGatewayImpl.getPluginPackageDirectory(
            pluginManifestRoom
        )
        val webUIEntryPoint = pluginManifestRoom.webUIEntryPoint!!

        return "$pluginPackageDirectory/$webUIEntryPoint"
    }
}
