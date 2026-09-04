package com.baidaidai.rootless_store.ui.navigation.model

import com.baidaidai.rootless_store.ui.navigation.`interface`.RootlessNavigationKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExecuteScreenKey(
    @SerialName("pluginID")
    val pluginId: String,
    @SerialName("isExecutePlugin")
    val shouldExecuteImmediately: Boolean = false
): RootlessNavigationKey
