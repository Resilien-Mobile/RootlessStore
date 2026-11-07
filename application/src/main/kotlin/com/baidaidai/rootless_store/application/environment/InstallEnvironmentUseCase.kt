package com.baidaidai.rootless_store.application.environment

import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.environment.gateway.EnvironmentGateway
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentRepository
import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import javax.inject.Inject
import java.io.File

class InstallEnvironmentUseCase @Inject constructor(
    private val androidFileSystemDefaultOperatorGateway: AndroidFileSystemDefaultOperatorGateway,
    private val environmentGateway: EnvironmentGateway,
    private val environmentRepository: EnvironmentRepository,
    private val environmentStatusRepository: EnvironmentStatusRepository
) {

    suspend operator fun invoke(): Result<Unit, PluginError> {
        val packageDirectory = androidFileSystemDefaultOperatorGateway
            .getExternalAppPackageCacheDirectoryFile()
        val localPackageFile = File(packageDirectory, "package.zip")

        return invoke(localPackageFile)
    }

    suspend operator fun invoke(packageFile: File): Result<Unit, PluginError> {
        val environmentManifest: EnvironmentManifest = environmentGateway
            .parseEnvironmentManifest(packageFile)
            .getOrElse { throwable ->
                return Err(
                    PluginError(
                        errorMessage = "Can't parse environment manifest",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "Rootless Store could not read EnvironmentManifest.json, or the manifest schema does not match the current environment format."
                    )
                )
            }

        // Un-Zip, Install Environment
        environmentGateway
            .installEnvironmentFromLocal(packageFile)
            .getOrElse { throwable ->
                return Err(
                    PluginError(
                        errorMessage = "Can't Un-Zip / install Environment",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "The package was recognized as an environment, but Rootless Store could not extract it into app storage."
                    )
                )
            }

        // Set Execute-able, Made it can call and use
        environmentGateway
            .setEnvironmentEntryPointExecutable(environmentManifest)
            .getOrElse { throwable ->
                return Err(
                    PluginError(
                        errorMessage = "Can't set environment executable",
                        errorCause = throwable.stackTrace.formatAsMultilineString(),
                        errorCompanion = "The environment was extracted, but Rootless Store could not mark its entry point executable."
                    )
                )
            }

        // Add Data, Register Environment
        environmentRepository.addEnvironment(environmentManifest)
        environmentStatusRepository.registerEnvironmentStatus(environmentManifest.environmentId, PluginOrigin.Local)

        return Ok(Unit)
    }
}
