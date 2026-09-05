package com.baidaidai.rootless_store.data.plugin.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

@Entity(tableName = "pluginStatus")
data class PluginStatusEntity(
    @PrimaryKey
    @ColumnInfo(name = "pluginID")
    val pluginId: String,

    @ColumnInfo(name = "enabled")
    val isEnabled: Boolean,

    val state: PluginState,
    val origin: PluginOrigin
)
