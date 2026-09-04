package com.baidaidai.rootless_store.ui.components.sourceScreen

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
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.core.i18n.icuString

object SourceScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun SourceScreenTopAppBar(
        onEditClick:()-> Unit = {},
        onAddClick:()-> Unit = {},
        pluginSourceCount: Int = 0
    ){
        LargeFlexibleTopAppBar(
            title = {
                Text(stringResource(R.string.sources_screen_top_app_bar_title))
            },
            subtitle = {
                Text(
                    text = icuString(
                        R.string.sources_screen_top_app_bar_subtitle,
                        mapOf("sourceCount" to pluginSourceCount)
                    )
                )
            },
            navigationIcon = {
                TextButton(
                    onClick = onEditClick
                ) {
                    Text(stringResource(R.string.sources_screen_top_app_bar_edit_button))
                }
            },
            actions = {
                IconButton(
                    onClick = onAddClick
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
