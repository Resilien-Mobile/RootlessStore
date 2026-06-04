package com.baidaidai.rootless_store.domain.navigation.model

import androidx.compose.ui.graphics.painter.Painter
import com.baidaidai.rootless_store.domain.navigation.`interface`.RootlessNavigationKey
import kotlin.reflect.KClass

data class NavBarItemSpec(
    val number: Int,
    val pattern: Painter,
    val contentDeprecated: String,
    val targetDestination: RootlessNavigationKey,
    val compatibleDestinationList: List<KClass<out RootlessNavigationKey>>
)