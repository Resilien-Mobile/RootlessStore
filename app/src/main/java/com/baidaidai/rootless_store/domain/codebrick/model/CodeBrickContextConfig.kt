package com.baidaidai.rootless_store.domain.codebrick.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

data class CodeBrickContextConfig(
    val contextType: ExecutionContext,
    @StringRes
    val contextTextResource: Int,

    @DrawableRes
    val contextIcon: Int
)
