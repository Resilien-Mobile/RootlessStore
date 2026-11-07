package com.baidaidai.rootless_store.domain.environment.gateway

import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import java.io.File

interface EnvironmentGateway {

    fun installEnvironmentFromLocal(
        originFile: File
    ): Result<Unit>

    suspend fun installEnvironmentFromMarket(
        environmentUrl: String,
        environmentManifest: EnvironmentManifest
    )

    fun uninstallEnvironment(
        environmentPackageName: String
    )

    fun setEnvironmentEntryPointExecutable(
        environmentManifest: EnvironmentManifest
    ): Result<Unit>

    fun resolveEnvironmentRuntimePath(
        environmentManifest: EnvironmentManifest
    ): String

    fun resolveEnvironmentLdPath(
        environmentManifest: EnvironmentManifest
    ): String

    fun resolveEnvironmentConfig(
        environmentManifest: EnvironmentManifest
    ): Map<String, String>

    fun parseEnvironmentManifest(
        originFile: File
    ): Result<EnvironmentManifest>

}
