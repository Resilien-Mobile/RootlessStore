package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import javax.inject.Inject

class InstallEnvironmentFromMarketUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val environmentGatewayImpl: EnvironmentGatewayImpl
) {
    suspend operator fun invoke(
        environmentUri: String,
        environmentManifestRemote: EnvironmentManifestRemote
    ) {
        environmentGatewayImpl.installEnvironmentFromMarket(environmentUri, environmentManifestRemote)
        environmentGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifestRemote)
        environmentRepositoryImpl.insertEnvironment(environmentManifestRemote)
    }
}
