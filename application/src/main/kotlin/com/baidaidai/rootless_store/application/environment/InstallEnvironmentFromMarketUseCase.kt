package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.domain.environment.gateway.EnvironmentGateway
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentRepository
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import javax.inject.Inject

class InstallEnvironmentFromMarketUseCase @Inject constructor(
    private val environmentRepository: EnvironmentRepository,
    private val environmentStatusRepository: EnvironmentStatusRepository,
    private val environmentGateway: EnvironmentGateway
) {
    suspend operator fun invoke(
        environmentUrl: String,
        environmentManifest: EnvironmentManifest
    ) {
        environmentGateway.installEnvironmentFromMarket(environmentUrl, environmentManifest)
        environmentGateway.setEnvironmentEntryPointExecutable(environmentManifest)
        environmentRepository.addEnvironment(environmentManifest)
        environmentStatusRepository.registerEnvironmentStatus(environmentManifest.environmentId, PluginOrigin.Official)
    }
}
