package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.baidaidai.rootless_store.R

object CodeBrickScreenNecessaryComponents {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun CodeBrickScreenTopAppBar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        MediumFlexibleTopAppBar(
            title = {
                Text(stringResource(R.string.code_brick_screen_top_app_bar_title))
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    fun CodeBrickScreenFloatingButton(
        onClick: () -> Unit = {}
    ) {
        FloatingActionButton(
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_add),
                contentDescription = stringResource(R.string.code_brick_screen_floating_button_add_content_description)
            )
        }
    }
}
