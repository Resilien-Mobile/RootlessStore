package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

interface EnvironmentManifest {
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

    val environmentID: String

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
    val iconURI: String?

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
     * Required hoster environment status / capability requirement.
     *
     * Meaning:
     * - Describes what environment capability this environment needs to run correctly.
     * - The host app should validate this before installation or before enabling.
     *
     * Note:
     * - If this is purely a *computed* runtime value (not declared by plugin),
     *   move it out of the manifest.
     */
    val requiredEnvironment: HosterOverallStatus

    val entryPoint: String

    val ldLibraryPath: List<String>

    val env: Map<String, String>

    // Runtime state such as `enabled`, `state`, `source` should NOT belong here:
    // - enabled: Boolean
    // - state: PluginState
    // - source: PluginSource
    interface EnvironmentManifestLocal: EnvironmentManifest
    interface EnvironmentManifestRemote: EnvironmentManifest {
        val environmentURI: String
    }
    interface EnvironmentManifestRoom: EnvironmentManifest {
        val enabled: Boolean
        val state: PluginState
        val source: PluginSource
    }
}