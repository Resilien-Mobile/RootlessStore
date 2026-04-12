package com.baidaidai.rootless_store.components.shellScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

object ShellScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShellScreenScreenTopAppBar(){
        TopAppBar(
            title = {
                Text("ShellScreen")
            }
        )
    }
}