package com.baidaidai.rootless_store.ui.navigation.model

import androidx.compose.ui.graphics.painter.Painter
import com.baidaidai.rootless_store.ui.navigation.`interface`.RootlessNavigationKey
import kotlin.reflect.KClass

data class NavBarItemSpec(
    val number: Int,
    val icon: Painter,
    val label: String,
    val targetDestination: RootlessNavigationKey,
    val compatibleDestinations: List<KClass<out RootlessNavigationKey>>
)
