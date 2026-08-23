package com.baidaidai.rootless_store.data.plugin.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

@Entity(tableName = "pluginInfo")
data class PluginEntity(

    /**
     * pluginId is primaryKey
     *
     * See more infos
     * @example com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
    */
    @PrimaryKey
    @ColumnInfo(name = "pluginID")
    val pluginId: String,

    // Plugin Basic Infos
    val installedVersion: String,
    val pluginRenderingName: String,
    val pluginPackageName: String,
    @ColumnInfo(name = "iconURI")
    val iconUri: String?,
    val author: String,
    val pluginDescription:String,

    // Plugin Runtime Infos
    @ColumnInfo(name = "enabled")
    val isEnabled: Boolean,
    val requiredEnvironment: ExecutionContext,
    val state: PluginState,
    @ColumnInfo(name = "source")
    val origin: PluginOrigin,
    val entryPoint: String,
    val pluginRunModel: PluginRunModel,
    @ColumnInfo(name = "webUIEntryPoint")
    val webUiEntryPoint: String?
)
