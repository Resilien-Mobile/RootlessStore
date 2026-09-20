package com.baidaidai.rootless_store.ui.components.marketScreen

import com.baidaidai.rootless_store.ui.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

object MarketScreenNecessaryComponents {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun MarketScreenTopAppBar(
        onBackClick: () -> Unit = {},
        onSearchClick: () -> Unit = {},
        onFilterClick: () -> Unit = {},
        sourceName: String = "Null",
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        MediumFlexibleTopAppBar(
            title = {
                Text(sourceName)
            },
            subtitle = {
                Text(stringResource(R.string.market_screen_top_app_bar_subtitle))
            },
            actions = {
                IconButton(
                    onClick = onSearchClick
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_search),
                        contentDescription = stringResource(R.string.market_screen_top_app_bar_search_content_description)
                    )
                }
                IconButton(
                    onClick = onFilterClick
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_filter_list),
                        contentDescription = stringResource(R.string.market_screen_top_app_bar_filter_content_description)
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_arrow_back),
                        contentDescription = stringResource(R.string.market_screen_top_app_bar_back_content_description)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

}

//@Composable
//@PreviewLightDark
//fun _MarketScreenTopAppBarPreview_(){
//    MarketScreenNecessaryComponents.MarketScreenTopAppBar()
//}
