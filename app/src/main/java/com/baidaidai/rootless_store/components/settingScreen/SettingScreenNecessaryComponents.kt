package com.baidaidai.rootless_store.components.settingScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.baidaidai.rootless_store.R

object SettingScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun SettingScreenTopAppBar(
        scrollBehavior: TopAppBarScrollBehavior
    ){
        MediumFlexibleTopAppBar(
            title = {
                Text(stringResource(R.string.setting_screen_top_app_bar_title))
            },
            scrollBehavior = scrollBehavior
        )
    }
}
