package com.baidaidai.rootless_store.components.sourcesScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.core.i18n.icuString

object SourcesScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun SourcesScreenTopAppBar(
        textButtonOnClick:()-> Unit = {},
        iconButtonOnClick:()-> Unit = {},
        sourceCount: Int = 0
    ){
        LargeFlexibleTopAppBar(
            title = {
                Text(stringResource(R.string.sources_screen_top_app_bar_title))
            },
            subtitle = {
                Text(
                    text = icuString(
                        R.string.sources_screen_top_app_bar_subtitle,
                        mapOf("sourceCount" to sourceCount)
                    )
                )
            },
            navigationIcon = {
                TextButton(
                    onClick = textButtonOnClick
                ) {
                    Text(stringResource(R.string.sources_screen_top_app_bar_edit_button))
                }
            },
            actions = {
                IconButton(
                    onClick = iconButtonOnClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_24px),
                        contentDescription = stringResource(R.string.sources_screen_top_app_bar_add_content_description)
                    )
                }
            }
        )
    }
}