package com.baidaidai.rootless_store.data.plugin.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

@Entity(tableName = "environmentInfo")
data class EnvironmentInfoEntity(

    /**
     * environmentID is primaryKey
     *
     * See more infos
     * @example com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifest
     */
    @PrimaryKey
    val environmentID: String,

    // Environment Basic Infos
    val installedVersion: String,
    val environmentRenderingName: String,
    val environmentPackageName: String,
    val iconURI: String?,
    val author: String,
    val environmentDescription:String,

    // Environment Runtime Infos
    val enabled: Boolean,
    val requiredEnvironment: HosterOverallStatus,
    val state: PluginState,
    val source: PluginSource,
    val entryPoint: String,
    val ldLibraryPath: List<String>,
    val env: Map<String, String>
){
    companion object {

        /**
         * Create an EnvironmentInfoEntity from EnvironmentManifest.
         *
         * This is the single source of truth for mapping
         * manifest data into database entity.
         */
        fun fromEnvironmentManifestRoom(
            environmentManifestRoom: EnvironmentManifestRoom
        ): EnvironmentInfoEntity =
            EnvironmentInfoEntity(
                environmentID = environmentManifestRoom.environmentID,

                // Basic Infos
                installedVersion = environmentManifestRoom.installedVersion,
                environmentRenderingName = environmentManifestRoom.environmentRenderingName,
                environmentPackageName = environmentManifestRoom.environmentPackageName,
                iconURI = environmentManifestRoom.iconURI,
                author = environmentManifestRoom.author,
                environmentDescription = environmentManifestRoom.environmentDescription,

                // Runtime Infos
                enabled = false,
                requiredEnvironment = environmentManifestRoom.requiredEnvironment,
                state = environmentManifestRoom.state,
                source = environmentManifestRoom.source,
                entryPoint = environmentManifestRoom.entryPoint,
                ldLibraryPath = environmentManifestRoom.ldLibraryPath,
                env = environmentManifestRoom.env
            )
    }
}