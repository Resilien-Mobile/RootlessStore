package com.baidaidai.rootless_store.components.executeScreen


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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.baidaidai.rootless_store.R

object executeScreenNecessaryComponents {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    fun ExecuteScreenTopAppBar(
        scrollBehavior: TopAppBarScrollBehavior,
        onExecuteScreenStopButtonClick: () -> Unit = {},
        onExecuteScreenShareButtonClick: () -> Unit = {},
        onExecuteScreenBackButtonClick: () -> Unit = {}
    ){
        MediumFlexibleTopAppBar(
            title = { Text(stringResource(R.string.execute_screen_top_app_bar_title)) },
            scrollBehavior = scrollBehavior,
            actions = {
                ExecuteScreenStopButton(onExecuteScreenStopButtonClick)
                ExecuteScreenShareButton(onExecuteScreenShareButtonClick)
            },
            navigationIcon = {
                ExecuteScreenBackButton(onExecuteScreenBackButtonClick)
            }
        )
    }

    @Composable
    private fun ExecuteScreenShareButton(
        onExecuteScreenShareButtonClick:()-> Unit = {}
    ) {
        IconButton(
            onClick = onExecuteScreenShareButtonClick
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_ios_share),
                contentDescription = stringResource(R.string.execute_screen_top_app_bar_share_content_description)
            )
        }
    }

    @Composable
    private fun ExecuteScreenStopButton(
        onExecuteScreenStopButtonClick:()-> Unit = {}
    ) {
        IconButton(
            onClick = onExecuteScreenStopButtonClick
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_disabled),
                contentDescription = stringResource(R.string.execute_screen_top_app_bar_stop_content_description)
            )
        }
    }

    @Composable
    private fun ExecuteScreenBackButton(
        onExecuteScreenBackButtonClick:()-> Unit = {}
    ) {
        IconButton(
            onClick = onExecuteScreenBackButtonClick
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_arrow_back),
                contentDescription = stringResource(R.string.execute_screen_top_app_bar_back_content_description)
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun test(){
    executeScreenNecessaryComponents.ExecuteScreenTopAppBar(
        scrollBehavior = androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}
