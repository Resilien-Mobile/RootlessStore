package com.baidaidai.rootless_store.application.environment

import android.net.Uri
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import javax.inject.Inject

class InstallEnvironmentUseCase @Inject constructor(
    private val environmentGatewayImpl: EnvironmentGatewayImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {
            val environmentManifest = environmentGatewayImpl.parseEnvironmentManifest(uri)
            // Un-Zip, Install Environment
            environmentGatewayImpl.installEnvironmentFromLocal(uri)
            // Set Execute-able, Made it can call and use
            environmentGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifest = environmentManifest)
            // Add Data, Register Environment
            environmentRepositoryImpl.addEnvironment(environmentManifest)
            environmentStatusRepositoryImpl.registerEnvironmentStatus(environmentManifest.environmentId, PluginOrigin.Local)

            null
        }catch (error: Throwable){
            PluginError(
                errorMessage = "",
                errorCause = ""
            )
        }
    }
}
