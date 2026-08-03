package com.baidaidai.rootless_store.data.plugin.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
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
    val entryPoint: String,
    val pluginRunModel: PluginRunModel,
    val webUIEntryPoint: String?
)
