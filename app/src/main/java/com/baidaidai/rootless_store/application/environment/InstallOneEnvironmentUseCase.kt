package com.baidaidai.rootless_store.application.environment

import android.net.Uri
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import javax.inject.Inject

class InstallOneEnvironmentUseCase @Inject constructor(
    private val environmentGatewayImpl: EnvironmentGatewayImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        return try {
            val environmentManifestLocal = environmentGatewayImpl.getRawEnvironmentManifestLocal(uri)
            // Un-Zip, Install Environment
            environmentGatewayImpl.installEnvironmentFromLocal(uri)
            // Set Execute-able, Made it can call and use
            environmentGatewayImpl.setEnvironmentEntryPointExecutable(environmentManifest = environmentManifestLocal)
            // Add Data, Register Environment
            environmentRepositoryImpl.insertOneEnvironmentInfo(environmentManifestLocal)

            null
        }catch (error: Throwable){
            PluginError(
                errorMessage = "",
                errorCause = ""
            )
        }
    }
}