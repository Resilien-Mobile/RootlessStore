package com.baidaidai.rootless_store.domain.navigation.model

import com.baidaidai.rootless_store.domain.navigation.`interface`.RootlessNavigationKey
import kotlinx.serialization.Serializable

@Serializable
data class ExecuteScreenKey(
    val pluginID: String,
    val isExecutePlugin: Boolean = false
): RootlessNavigationKey