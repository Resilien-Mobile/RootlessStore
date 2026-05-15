package com.baidaidai.rootless_store.components.marketScreen

import com.baidaidai.rootless_store.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

object MarketScreenNecessaryComponents {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun MarketScreenScreenTopAppBar(
        onBackButtonClick: ()-> Unit = {},
        onSearchButtonClick: ()-> Unit = {},
        onFilterButtonClick: ()-> Unit = {},
        sourceName: String = "Null",
        scrollBehavior: TopAppBarScrollBehavior
    ){
        MediumFlexibleTopAppBar(
            title = {
                Text(sourceName)
            },
            subtitle = {
                Text("Total Plugin: 0 available")
            },
            actions = {
                IconButton(
                    onClick = onSearchButtonClick
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_search),
                        contentDescription = "Search"
                    )
                }
                IconButton(
                    onClick = onFilterButtonClick
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_filter_list),
                        contentDescription = "Filter"
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackButtonClick
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_arrow_back),
                        contentDescription = "Back"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

}

//@Composable
//@PreviewLightDark
//fun _MarketScreenScreenTopAppBarPreview_(){
//    MarketScreenNecessaryComponents.MarketScreenScreenTopAppBar()
//}