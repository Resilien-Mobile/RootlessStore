package com.baidaidai.rootless_store.domain.codebrick.model

import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class CodeBrickConfig(
    val unixTimestamp: Long,
    val codeBrickTitle: String,
    val codeBrickEnvironment: HosterOverallStatus,
    val codeBrickContent: String,
    val boundTileIndex: Int? = null
)