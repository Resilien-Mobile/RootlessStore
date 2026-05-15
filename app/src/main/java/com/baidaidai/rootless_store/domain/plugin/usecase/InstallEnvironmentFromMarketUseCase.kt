package com.baidaidai.rootless_store.domain.plugin.usecase

import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.manifest.RootlessStoreManifestCollection
import javax.inject.Inject

class InstallEnvironmentFromMarketUseCase @Inject constructor(
    private val pluginCoreRepositoryImpl: PluginCoreRepositoryImpl
) {
    suspend operator fun invoke(
        environmentURI: String,
        manifest: RootlessStoreManifestCollection
    ) = pluginCoreRepositoryImpl.installOnePluginFromMarket(environmentURI,manifest)
}