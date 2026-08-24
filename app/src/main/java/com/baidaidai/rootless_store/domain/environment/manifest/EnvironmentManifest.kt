package com.baidaidai.rootless_store.domain.environment.manifest

import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

sealed interface EnvironmentManifest: MarketManifest {
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
    val installedVersion: String

    val environmentRenderingName: String

    val environmentPackageName: String

    val environmentId: String

    /**
     * Environment icon reference shown in the environment list.
     *
     * Optional:
     * - `null` means "no icon".
     *
     * Suggested formats (pick one and standardize later):
     * - Content URI: "content://..."
     * - File URI/path: "file://..." or "/icons/xxx.png"
     * - Relative path inside plugin package/zip: "icons/icon.png"
     */
    val iconUri: String?

    /**
     * Environment author / publisher name.
     *
     * Example: "Alice", "Baidaidai", "Rootless Team"
     */
    val author: String

    /**
     * A short description shown in details pages.
     *
     * Tips:
     * - Keep it concise (1–3 sentences).
     * - Avoid extremely long text; store long-form docs elsewhere if needed.
     */
    val environmentDescription: String

    // ─────────────────────────────────────────────────────────────
    // Environment Runtime / Compatibility Infos
    // ─────────────────────────────────────────────────────────────

    /**
     * Required execution context or capability requirement.
     *
     * Meaning:
     * - Describes what environment capability this environment needs to run correctly.
     * - The host app should validate this before installation or before enabling.
     *
     * Note:
     * - If this is purely a *computed* runtime value (not declared by plugin),
     *   move it out of the manifest.
     */
    val requiredEnvironment: ExecutionContext

    val entryPoint: String

    val ldLibraryPath: List<String>

    val env: Map<String, String>

    // Runtime state such as `isEnabled`, `state`, `origin` should NOT belong here:
    // - isEnabled: Boolean
    // - state: PluginState
    // - origin: PluginOrigin
    interface EnvironmentManifestLocal: EnvironmentManifest
    interface EnvironmentManifestRemote: EnvironmentManifest {
        val environmentUrl: String
    }
    interface EnvironmentManifestRoom: EnvironmentManifest {
        val isEnabled: Boolean
        val state: PluginState
        val origin: PluginOrigin
    }
}
