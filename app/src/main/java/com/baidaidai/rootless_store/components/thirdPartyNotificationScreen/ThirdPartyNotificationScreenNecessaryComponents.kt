package com.baidaidai.rootless_store.components.thirdPartyNotificationScreen

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.baidaidai.rootless_store.R

object ThirdPartyNotificationScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun ThirdPartyNotificationScreenTopAppBar(
        scrollBehavior: TopAppBarScrollBehavior,
        onSaveButtonClick: ()->Unit = {}
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text("Third-Party Notification")
            },
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(
                    onClick = onSaveButtonClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_save),
                        contentDescription = "save"
                    )
                }
            }
        )
    }
}