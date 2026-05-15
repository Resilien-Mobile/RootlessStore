package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

sealed interface PluginManifest: RootlessStoreManifestCollection {

    // ─────────────────────────────────────────────────────────────
    // Plugin Basic Infos
    // ─────────────────────────────────────────────────────────────

    /**
     * The installed version of the plugin (e.g., "1.2.19").
     *
     * Notes:
     * - Should follow SemVer if possible: MAJOR.MINOR.PATCH
     * - Used for display, updates, and compatibility checks.
     */
    val installedVersion: String

    /**
     * The display name shown in the plugin list UI.
     *
     * You can choose any human-friendly name.
     * Example: "Zip Tools", "Kernel Patch", "Nice Plugin"
     */
    val pluginRenderingName: String

    /**
     * The package name (identifier) of the plugin on the user's device.
     *
     * Requirements:
     * - Must be a valid Android-style package name, recommended format:
     *   `lowercase.separated.by.dots`
     * - Avoid spaces and special characters like `(`, `)`, `!`, `?`, `/`, `.`, etc.
     * - Should be stable across versions.
     *
     * Example: "com.example.myplugin"
     */
    val pluginPackageName: String

    /**
     * Primary key used by Room (plugin unique ID).
     *
     * Recommendations:
     * - Must be stable for the same plugin across installs/updates.
     * - Prefer a strong unique value:
     *   - SHA-256 hash of (packageName + author + something stable), or
     *   - a UUID generated once and persisted in the manifest.
     *
     * Why “more random” helps:
     * - Higher entropy reduces collision probability, which reduces DB key conflicts.
     */
    val pluginID: String

    /**
     * Plugin icon reference shown in the plugin list.
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
     * Plugin author / publisher name.
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
    val pluginDescription: String

    // ─────────────────────────────────────────────────────────────
    // Plugin Runtime / Compatibility Infos
    // ─────────────────────────────────────────────────────────────

    /**
     * Required hoster environment status / capability requirement.
     *
     * Meaning:
     * - Describes what environment the plugin needs to run correctly.
     * - The host app should validate this before installation or before enabling.
     *
     * Note:
     * - If this is purely a *computed* runtime value (not declared by plugin),
     *   move it out of the manifest.
     */
    val requiredEnvironment: HosterOverallStatus

    val entryPoint: String

    // Runtime state such as `enabled`, `state`, `source` should NOT belong here:
    // - enabled: Boolean
    // - state: PluginState
    // - source: PluginSource
    interface PluginManifestLocal: PluginManifest
    interface PluginManifestRemote: PluginManifest {
        val pluginURI: String
        val pluginRunModel: PluginRunModel
    }
    interface PluginManifestRoom: PluginManifest {
        val enabled: Boolean
        val state: PluginState
        val source: PluginSource
    }
}