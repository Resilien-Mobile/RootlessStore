package com.baidaidai.rootless_store.data.environment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.model.PluginSource
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

@Entity(tableName = "environmentInfo")
data class EnvironmentInfoEntity(

    /**
     * environmentId is primaryKey
     *
     * See more infos
     * @example com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
    */
    @PrimaryKey
    @ColumnInfo(name = "environmentID")
    val environmentId: String,

    // Environment Basic Infos
    val installedVersion: String,
    val environmentRenderingName: String,
    val environmentPackageName: String,
    @ColumnInfo(name = "iconURI")
    val iconUri: String?,
    val author: String,
    val environmentDescription:String,

    // Environment Runtime Infos
    @ColumnInfo(name = "enabled")
    val isEnabled: Boolean,
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
                environmentId = environmentManifestRoom.environmentId,

                // Basic Infos
                installedVersion = environmentManifestRoom.installedVersion,
                environmentRenderingName = environmentManifestRoom.environmentRenderingName,
                environmentPackageName = environmentManifestRoom.environmentPackageName,
                iconUri = environmentManifestRoom.iconUri,
                author = environmentManifestRoom.author,
                environmentDescription = environmentManifestRoom.environmentDescription,

                // Runtime Infos
                isEnabled = false,
                requiredEnvironment = environmentManifestRoom.requiredEnvironment,
                state = environmentManifestRoom.state,
                source = environmentManifestRoom.source,
                entryPoint = environmentManifestRoom.entryPoint,
                ldLibraryPath = environmentManifestRoom.ldLibraryPath,
                env = environmentManifestRoom.env
            )
    }
}
