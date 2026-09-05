package com.baidaidai.rootless_store.data.environment.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState

@Entity(tableName = "environmentStatus")
data class EnvironmentStatusEntity(
    @PrimaryKey
    @ColumnInfo(name = "environmentID")
    val environmentId: String,

    @ColumnInfo(name = "enabled")
    val isEnabled: Boolean,

    val state: PluginState,
    val origin: PluginOrigin
)
