package com.baidaidai.rootless_store.domain.navigation.model

import com.baidaidai.rootless_store.domain.navigation.`interface`.RootlessNavigationKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExecuteScreenKey(
    @SerialName("pluginID")
    val pluginId: String,
    val isExecutePlugin: Boolean = false
): RootlessNavigationKey
