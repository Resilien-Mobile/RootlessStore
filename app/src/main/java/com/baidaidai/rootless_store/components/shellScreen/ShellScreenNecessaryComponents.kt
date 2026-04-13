package com.baidaidai.rootless_store.components.shellScreen

import com.baidaidai.rootless_store.R
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
    fun ShellScreenScreenTopAppBar(
        onTopIconClick:suspend ()-> Unit = {},
        onBottomIconClick:suspend ()-> Unit = {},
        onDeleteIconClick:()-> Unit = {},
    ){
        val coroutineScope = rememberCoroutineScope()

        TopAppBar(
            title = {
                Text("ShellScreen")
            },
            actions = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onTopIconClick()
                        }
                    }
                ){
                    Icon(
                        painterResource(R.drawable.material_symbols_top),
                        contentDescription = "To Top"
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onBottomIconClick()
                        }
                    }
                ){
                    Icon(
                        painterResource(R.drawable.material_symbols_bottom),
                        contentDescription = "To Top"
                    )
                }
                IconButton(onClick = onDeleteIconClick){
                    Icon(
                        painterResource(R.drawable.material_symbols_delete),
                        contentDescription = "To Top"
                    )
                }
            }
        )
    }
}