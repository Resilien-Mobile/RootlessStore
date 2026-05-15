package com.baidaidai.rootless_store.data.plugin.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

@Entity(tableName = "pluginInfo")
data class PluginInfoEntity(

    /**
     * pluginID is primaryKey
     *
     * See more infos
     * @example com.baidaidai.rootless_store.domain.pluginManiFest.model.PluginMainFest
     */
    @PrimaryKey
    val pluginID: String,

    // Plugin Basic Infos
    val installedVersion: String,
    val pluginRenderingName: String,
    val pluginPackageName: String,
    val iconURI: String?,
    val author: String,
    val pluginDescription:String,

    // Plugin Runtime Infos
    val enabled: Boolean,
    val requiredEnvironment: HosterOverallStatus,
    val state: PluginState,
    val source: PluginSource,
    val entryPoint: String
){
    companion object {

        /**
         * Create a PluginInfoEntity from PluginManiFest.
         *
         * This is the single source of truth for mapping
         * manifest data into database entity.
         */
        fun fromPluginManifestRoom(
            pluginManifestRoom: PluginManifestRoom
        ): PluginInfoEntity =
            PluginInfoEntity(
                pluginID = pluginManifestRoom.pluginID,

                // Basic Infos
                installedVersion = pluginManifestRoom.installedVersion,
                pluginRenderingName = pluginManifestRoom.pluginRenderingName,
                pluginPackageName = pluginManifestRoom.pluginPackageName,
                iconURI = pluginManifestRoom.iconURI,
                author = pluginManifestRoom.author,
                pluginDescription = pluginManifestRoom.pluginDescription,

                // Runtime Infos
                enabled = false,
                requiredEnvironment = pluginManifestRoom.requiredEnvironment,
                state = pluginManifestRoom.state,
                source = pluginManifestRoom.source,
                entryPoint = pluginManifestRoom.entryPoint
            )
    }
}