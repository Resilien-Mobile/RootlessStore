package com.baidaidai.rootless_store.data.codebrick.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

@Entity
data class CodeBrickEntity(
    @PrimaryKey
    @ColumnInfo(name = "unixTimeStamp")
    val unixTimestamp: Long,

    val codeBrickTitle: String,
    val codeBrickEnvironment: ExecutionContext,
    val codeBrickContent: String,
    @ColumnInfo(name = "bindTileIndex")
    val boundTileIndex: Int? = null
)
