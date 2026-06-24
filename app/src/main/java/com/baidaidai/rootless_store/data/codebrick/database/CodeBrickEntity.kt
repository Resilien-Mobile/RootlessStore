package com.baidaidai.rootless_store.data.codebrick.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

@Entity
data class CodeBrickEntity(
    @PrimaryKey
    val unixTimeStamp: Long,

    val codeBrickTitle: String,
    val codeBrickEnvironment: HosterOverallStatus,
    val codeBrickContent: String
)
