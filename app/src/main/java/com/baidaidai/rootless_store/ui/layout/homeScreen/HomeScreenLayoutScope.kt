package com.baidaidai.rootless_store.ui.layout.homeScreen

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

class HomeScreenLayoutScope(
    val preferWidth: Dp
){
    fun getBasicWidthModifier(
        modifier: Modifier? = null
    ): Modifier{
        val preferWidth = preferWidth

        return modifier?.width(preferWidth) ?: Modifier.width(preferWidth)
    }
}