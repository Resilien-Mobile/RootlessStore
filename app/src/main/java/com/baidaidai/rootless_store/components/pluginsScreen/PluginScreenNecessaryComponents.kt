package com.baidaidai.rootless_store.components.pluginsScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.core.i18n.icuString

object PluginScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun PluginScreenScreenTopAppBar(
        textButtonOnClick:()-> Unit = {},
        iconButtonOnClick:()-> Unit = {},
        pluginInfoCount: Int = 0,
        scrollBehavior: TopAppBarScrollBehavior
    ){
        LargeFlexibleTopAppBar(
            title = {
                Text(stringResource(R.string.plugin_screen_top_app_bar_title))
            },
            subtitle = {
                Text(
                    text = icuString(
                        R.string.plugin_screen_top_app_bar_subtitle,
                        mapOf("pluginCount" to pluginInfoCount)
                    )
                )
            },
            navigationIcon = {
                TextButton(
                    onClick = textButtonOnClick
                ) {
                    Text(stringResource(R.string.plugin_screen_top_app_bar_edit_button))
                }
            },
            actions = {
                IconButton(
                    onClick = iconButtonOnClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_filter_list),
                        contentDescription = stringResource(R.string.plugin_screen_top_app_bar_filter_content_description)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    fun PluginScreenFloatingButton(
        onClick:()-> Unit
    ){
        FloatingActionButton(
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_box_add_24),
                contentDescription = stringResource(R.string.plugin_screen_floating_button_install_content_description)
            )
        }
    }
}