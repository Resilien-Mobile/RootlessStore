package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class CodeBrickConfig(
    val unixTimeStamp: Long,
    val codeBrickTitle: String,
    val codeBrickEnvironment: HosterOverallStatus,
    val codeBrickContent: String,
    val bindTileIndex: Int? = null
)