package com.baidaidai.rootless_store.data.environment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

@Entity(tableName = "environmentInfo")
data class EnvironmentEntity(

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
    val environmentDescription: String,

    // Environment Runtime Infos
    val requiredEnvironment: ExecutionContext,
    val entryPoint: String,
    val ldLibraryPath: List<String>,
    val env: Map<String, String>
)
