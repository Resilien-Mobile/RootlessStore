package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection
import javax.inject.Inject

class InstallEnvironmentFromMarketUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val environmentGatewayImpl: EnvironmentGatewayImpl
) {
    suspend operator fun invoke(
        environmentURI: String,
        manifest: ModuleManifestCollection
    ) {
        val environmentManifestRemote = manifest as EnvironmentManifestRemote

        environmentGatewayImpl.installEnvironmentFromMarket(environmentURI, environmentManifestRemote)
        environmentGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifestRemote)
        environmentRepositoryImpl.insertOneEnvironmentInfo(environmentManifestRemote)
    }
}