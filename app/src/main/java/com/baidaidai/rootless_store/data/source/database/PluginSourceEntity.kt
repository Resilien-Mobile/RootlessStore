package com.baidaidai.rootless_store.data.source.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMetadata

@Entity(tableName = "pluginSource")
data class PluginSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "sourceID")
    val sourceId: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    @ColumnInfo(name = "userAccessToken")
    val accessToken: String?,

    @Embedded
    val pluginSourceAuthenticationMetadata: PluginSourceAuthenticationMetadata,
)
