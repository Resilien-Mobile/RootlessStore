package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import javax.inject.Inject

class InstallEnvironmentFromMarketUseCase @Inject constructor(
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl,
    private val environmentGatewayImpl: EnvironmentGatewayImpl
) {
    suspend operator fun invoke(
        environmentUrl: String,
        environmentManifest: EnvironmentManifest
    ) {
        environmentGatewayImpl.installEnvironmentFromMarket(environmentUrl, environmentManifest)
        environmentGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifest)
        environmentRepositoryImpl.addEnvironment(environmentManifest)
        environmentStatusRepositoryImpl.registerEnvironmentStatus(environmentManifest.environmentId, PluginOrigin.Official)
    }
}
