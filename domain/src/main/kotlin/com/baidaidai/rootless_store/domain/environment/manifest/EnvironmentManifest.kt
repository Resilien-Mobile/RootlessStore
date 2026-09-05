package com.baidaidai.rootless_store.domain.environment.manifest

import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.serialization.Serializable

@Serializable
data class EnvironmentManifest(
    // ─────────────────────────────────────────────────────────────
    // Environment Basic Infos
    // ─────────────────────────────────────────────────────────────

    /**
     * The installed version of the environment (e.g., "1.2.19").
     *
     * Notes:
     * - Should follow SemVer if possible: MAJOR.MINOR.PATCH
     * - Used for display, updates, and compatibility checks.
     */
    val installedVersion: String,

    val environmentRenderingName: String,

    val environmentPackageName: String,

    /**
     * Primary key of the environment, stable across versions.
     */
    val environmentId: String,

    /**
     * Environment icon reference shown in the environment list.
     *
     * Optional:
     * - `null` means "no icon".
     */
    val iconUri: String?,

    /**
     * Environment author / publisher name.
     */
    val author: String,

    /**
     * A short description shown in details pages.
     */
    val environmentDescription: String,

    // ─────────────────────────────────────────────────────────────
    // Environment Runtime / Compatibility Infos
    // ─────────────────────────────────────────────────────────────

    /**
     * Required execution context or capability requirement.
     */
    val requiredEnvironment: ExecutionContext,

    val entryPoint: String,

    val ldLibraryPath: List<String>,

    val env: Map<String, String>,

    /**
     * Remote download URL, only meaningful for market manifests.
     * Not persisted.
     */
    val environmentUrl: String? = null
): MarketManifest
