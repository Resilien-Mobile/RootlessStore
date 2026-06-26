package com.baidaidai.rootless_store.domain.codebrick.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

data class CodeBrickContextConfig(
    val contextType: HosterOverallStatus,
    @StringRes val contextTextResource: Int,

    @DrawableRes
    val contextIcon: Int
)
