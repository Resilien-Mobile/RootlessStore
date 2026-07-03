package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickScreenNecessaryComponents.CodeBrickScreenFloatingButton

object CodeBrickScreenNecessaryComponents {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun CodeBrickScreenTopAppBar(
        scrollBehavior: TopAppBarScrollBehavior
    ) {
        LargeFlexibleTopAppBar(
            title = {
                Text(stringResource(R.string.code_brick_screen_top_app_bar_title))
            },
            subtitle = {
                Text(stringResource(R.string.code_brick_screen_top_app_bar_subtitle))
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    fun CodeBrickScreenFloatingButton(
        onHandMenuItemClick: ()-> Unit = {},
        onJsonMenuItemClick: ()-> Unit = {},
    ) {

        var buttonMenuExpandStatus by remember { mutableStateOf(false) }

        FloatingActionButtonMenu(
            expanded = buttonMenuExpandStatus,
            button = {
                ToggleFloatingActionButton(
                    checked = buttonMenuExpandStatus,
                    onCheckedChange = {
                        buttonMenuExpandStatus = it
                    }
                ) {
                    if (buttonMenuExpandStatus){
                        Icon(
                            painter = painterResource(R.drawable.outline_close_24),
                            contentDescription = stringResource(R.string.code_brick_screen_floating_button_add_content_description)
                        )
                    }else{
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_add),
                            contentDescription = stringResource(R.string.code_brick_screen_floating_button_add_content_description)
                        )
                    }
                }
            },
            modifier = Modifier
                .offset(16.dp,16.dp)
        ) {
            InputByJsonMenuItem { onJsonMenuItemClick() }
            InputByHandMenuItem { onHandMenuItemClick() }

        }
    }

    @Composable
    private fun FloatingActionButtonMenuScope.InputByJsonMenuItem(
        onClick: () -> Unit = {}
    ){
        FloatingActionButtonMenuItem(
            onClick = onClick,
            text = {
                Text("By Token")
            },
            icon = {
                Text("\uD83D\uDDE3\uFE0F")
            }
        )
    }

    @Composable
    private fun FloatingActionButtonMenuScope.InputByHandMenuItem(
        onClick: () -> Unit = {}
    ){
        FloatingActionButtonMenuItem(
            onClick = onClick,
            text = {
                Text("By Writing")
            },
            icon = {
                Text("✏\uFE0F")
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun _CodeBrickScreenFloatingButtonPreview_() {
    CodeBrickScreenFloatingButton()
}