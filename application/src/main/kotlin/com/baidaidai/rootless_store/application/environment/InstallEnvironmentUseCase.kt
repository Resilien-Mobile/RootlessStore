package com.baidaidai.rootless_store.application.environment

import android.net.Uri
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.data.environment.gateway.EnvironmentGatewayImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import javax.inject.Inject

class InstallEnvironmentUseCase @Inject constructor(
    private val environmentGatewayImpl: EnvironmentGatewayImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val environmentStatusRepositoryImpl: EnvironmentStatusRepositoryImpl
) {
    suspend operator fun invoke(uri: Uri): PluginError? {
        val environmentManifest: EnvironmentManifest = environmentGatewayImpl
            .parseEnvironmentManifest(uri)
            .getOrElse { throwable ->
                return PluginError(
                    errorMessage = "Can't parse environment manifest",
                    errorCause = throwable.stackTrace.formatAsMultilineString(),
                    errorCompanion = "Rootless Store could not read EnvironmentManifest.json, or the manifest schema does not match the current environment format."
                )
            }

        // Un-Zip, Install Environment
        environmentGatewayImpl
            .installEnvironmentFromLocal(uri)
            .onFailure { throwable ->
                return PluginError(
                    errorMessage = "Can't Un-Zip / install Environment",
                    errorCause = throwable.stackTrace.formatAsMultilineString(),
                    errorCompanion = "The package was recognized as an environment, but Rootless Store could not extract it into app storage."
                )
            }

        // Set Execute-able, Made it can call and use
        environmentGatewayImpl
            .setEnvironmentEntryPointExecutable(environmentManifest)
            .onFailure { throwable ->
                return PluginError(
                    errorMessage = "Can't set environment executable",
                    errorCause = throwable.stackTrace.formatAsMultilineString(),
                    errorCompanion = "The environment was extracted, but Rootless Store could not mark its entry point executable."
                )
            }

        // Add Data, Register Environment
        environmentRepositoryImpl.addEnvironment(environmentManifest)
        environmentStatusRepositoryImpl.registerEnvironmentStatus(environmentManifest.environmentId, PluginOrigin.Local)

        return null
    }
}
