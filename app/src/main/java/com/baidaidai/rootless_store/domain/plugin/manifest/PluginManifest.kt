package com.baidaidai.rootless_store.domain.plugin.manifest

import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

sealed interface PluginManifest: MarketManifest {

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
    val pluginId: String

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
    val iconUri: String?

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

    /**
     * The runtime model used when executing the plugin.
     *
     * Recommendations:
     * - Choose `OneTime` for scripts that produce no output, run once, or emit only a small amount of logs.
     * - Choose `Daemon` for long-running tasks, continuous output, or anything that needs persistent monitoring.
     *
     * Important:
     * - If the plugin is not clearly one-shot, you must choose `Daemon`.
     */
    val pluginRunModel: PluginRunModel

    val entryPoint: String

    /**
     * The optional Web UI entry point of the plugin.
     *
     * We follow the KernelSU-style `webroot` convention, while still allowing
     * plugin authors to choose a custom layout when needed.
     *
     * Usage:
     * - Set this to `null` if the plugin does not provide a Web UI.
     * - If the plugin provides a Web UI, set this to the relative path of
     *   `index.html` from the plugin package root.
     *
     * Example:
     * - `null`
     * - `"webroot/index.html"`
     * - `"ui/index.html"`
     */
    val webUiEntryPoint: String?

    // Runtime state such as `isEnabled`, `state`, `origin` should NOT belong here:
    // - isEnabled: Boolean
    // - state: PluginState
    // - origin: PluginOrigin
    interface PluginManifestLocal: PluginManifest {

        /**
         * Additional files that should be restored/marked as executable after extraction.
         *
         * Android packaging/extraction may not preserve Unix executable bits,
         * so the host should chmod these paths before running the plugin.
         *
         * Paths are relative to the plugin package root.
         */
        val executableFiles: List<String>?

    }
    interface PluginManifestRemote: PluginManifest {
        val pluginUri: String
    }
    interface PluginManifestRoom: PluginManifest {
        val isEnabled: Boolean
        val state: PluginState
        val origin: PluginOrigin
    }
}
