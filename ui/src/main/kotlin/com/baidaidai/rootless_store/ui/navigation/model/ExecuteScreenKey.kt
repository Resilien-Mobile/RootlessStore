package com.baidaidai.rootless_store.ui.navigation.model

import com.baidaidai.rootless_store.ui.navigation.`interface`.RootlessNavigationKey
import kotlinx.serialization.Serializable

@Serializable
data class ExecuteScreenKey(
    val pluginId: String,
    val shouldExecuteImmediately: Boolean = false
): RootlessNavigationKey
