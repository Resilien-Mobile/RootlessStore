package com.baidaidai.rootless_store.ui.components.shellScreen

import com.baidaidai.rootless_store.ui.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch

object ShellScreenNecessaryComponents {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShellScreenTopAppBar(
        onScrollToTop: suspend () -> Unit = {},
        onScrollToBottom: suspend () -> Unit = {},
        onClearOutput: () -> Unit = {},
    ) {
        val coroutineScope = rememberCoroutineScope()

        TopAppBar(
            title = {
                Text("ShellScreen")
            },
            actions = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onScrollToTop()
                        }
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_top),
                        contentDescription = "Scroll to top"
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onScrollToBottom()
                        }
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_bottom),
                        contentDescription = "Scroll to bottom"
                    )
                }
                IconButton(onClick = onClearOutput) {
                    Icon(
                        painterResource(R.drawable.material_symbols_delete),
                        contentDescription = "Clear output"
                    )
                }
            }
        )
    }
}
