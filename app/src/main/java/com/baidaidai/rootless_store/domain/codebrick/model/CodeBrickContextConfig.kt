package com.baidaidai.rootless_store.domain.codebrick.model

import androidx.annotation.DrawableRes
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class CodeBrickContextConfig(
    val contextType: HosterOverallStatus,
    val contextText: String,

    @DrawableRes
    val contextIcon: Int
)
