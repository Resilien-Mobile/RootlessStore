package com.baidaidai.rootless_store.ui.layout.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * @param modifier to control the inner FlowColumn's rendering behavior
 * @param contentPadding is NOT A REAL CONTENT PADDING!!!!!
 * Use Spacer to express it, Use it must Carefully! Default is 2.dp
 */
@Composable
fun HomeScreenExpandedLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 2.dp),
    preferWidth: Dp = 345.dp,
    content: @Composable HomeScreenLayoutScope.()-> Unit = {}
){
    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(
                    width = contentPadding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr)
                )
        )

        HomeScreenLayoutScope(preferWidth)
            .content()

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(
                    width = contentPadding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr)
                )
        )

    }
}

@PreviewLightDark
@Composable
private fun _preview_() {
    HomeScreenExpandedLayout(contentPadding = PaddingValues(16.dp))
}