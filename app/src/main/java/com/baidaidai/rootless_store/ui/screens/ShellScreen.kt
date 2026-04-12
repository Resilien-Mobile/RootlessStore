package com.baidaidai.rootless_store.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.components.shellScreen.ShellScreenNecessaryComponents
import com.baidaidai.rootless_store.domain.execute.model.ResultTag
import com.baidaidai.rootless_store.domain.shell.model.ShellCommandContainer
import com.baidaidai.rootless_store.domain.shell.model.ShellEnvironment
import com.baidaidai.rootless_store.ui.model.RootLessStoreShellScreenViewModel
import androidx.compose.ui.graphics.*

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShellScreen(
    contentPaddingValues: PaddingValues
){
    var commandContent by remember { mutableStateOf("") }
    var shellEnvironment by remember { mutableStateOf(ShellEnvironment.AppShell) }
    var trailingButtonStatus by remember { mutableStateOf(false) }

    val shellScreenViewModel = hiltViewModel<RootLessStoreShellScreenViewModel>()
    val shellOutputList by shellScreenViewModel.shellOutputList.collectAsState()

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .padding(vertical = 15.dp, horizontal = 15.dp)
            .fillMaxSize()
    ) {

        val trailingButtonContentPaddingAfterClick = PaddingValues(start = 15.dp, end = 15.dp)
        val trailingButtonContentPaddingBeforeClick = PaddingValues(start = 13.dp, end = 17.dp)
        val trailingButtonSizeBeforeClick = SplitButtonDefaults.trailingButtonShapesFor(56.dp).shape

        val trailingButtonContentPadding = remember(trailingButtonStatus) {
            if (trailingButtonStatus){
                trailingButtonContentPaddingAfterClick
            }else{
                trailingButtonContentPaddingBeforeClick
            }
        }

        val trailingButtonSize = remember(trailingButtonStatus) {
            if (trailingButtonStatus){
                CircleShape
            }else{
                trailingButtonSizeBeforeClick
            }
        }

        val shellCommandContainer = remember(key1 = commandContent, key2 = shellEnvironment) {
            ShellCommandContainer(shellEnvironment, commandContent = commandContent)
        }

        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            Column{
                OutlinedTextField(
                    value = commandContent,
                    onValueChange = {
                        commandContent = it
                    },
                    maxLines = 1,
                    label = {
                        Text("Shell Command")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    SplitButtonLayout(
                        leadingButton = {
                            Button(
                                onClick = {
                                    shellScreenViewModel.runCommand(shellCommandContainer)
                                },
                                contentPadding = SplitButtonDefaults.MediumLeadingButtonContentPadding,
                                shape = SplitButtonDefaults.leadingButtonShapesFor(
                                    SplitButtonDefaults.MediumContainerHeight).shape,
                                modifier = Modifier
                                    .height(56.dp)

                            ) {
                                Icon(
                                    painterResource(R.drawable.terminal_24px),
                                    contentDescription = "Run Command",
                                    modifier = Modifier
                                        .size(SplitButtonDefaults.LeadingIconSize)
                                )
                                Spacer(
                                    modifier = Modifier
                                        .size(8.dp)
                                )
                                Text(text = "Run")
                            }
                        },
                        trailingButton = {
                            Column{
                                Button(
                                    onClick = {
                                        trailingButtonStatus = !trailingButtonStatus
                                    },
                                    shape = trailingButtonSize,
                                    contentPadding = trailingButtonContentPadding,
                                    modifier = Modifier
                                        .height(56.dp)
                                ){
                                    Icon(
                                        painterResource(R.drawable.material_symbols_keyboard_arrow_down_icon),
                                        contentDescription = "Expand More",
                                        modifier = Modifier
                                            .size(26.dp)
                                            .rotate(if (trailingButtonStatus) 0f else -90f )
                                    )
                                }

                                DropdownMenuPopup(
                                    expanded = trailingButtonStatus,
                                    onDismissRequest = { trailingButtonStatus = !trailingButtonStatus},
                                    offset = DpOffset(x = 0.dp, y = 8.dp)
                                ) {
                                    DropdownMenuGroup(
                                        shapes = MenuDefaults.groupShapes(),
                                    ) {

                                        DropdownMenuItem(
                                            selected = shellEnvironment == ShellEnvironment.AppShell,
                                            shapes = MenuDefaults.itemShape(1,4),
                                            leadingIcon = {
                                                Icon(
                                                    painterResource(R.drawable.material_symbols_applicaitons),
                                                    contentDescription = "App shell"
                                                )
                                            },
                                            text = {
                                                Text("App Shell")
                                            },
                                            onClick = {
                                                shellEnvironment = ShellEnvironment.AppShell
                                            },
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        DropdownMenuItem(
                                            selected = shellEnvironment == ShellEnvironment.ADBShell,
                                            shapes = MenuDefaults.itemShape(2,4),
                                            leadingIcon = {
                                                Icon(
                                                    painterResource(R.drawable.material_symbols_adb),
                                                    contentDescription = "ADB shell"
                                                )
                                            },
                                            text = {
                                                Text("ADB shell")
                                            },
                                            onClick = {
                                                shellEnvironment = ShellEnvironment.ADBShell
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        DropdownMenuItem(
                                            selected = shellEnvironment == ShellEnvironment.RootShell,
                                            shapes = MenuDefaults.itemShape(3,4),
                                            leadingIcon = {
                                                Icon(
                                                    painterResource(R.drawable.material_symbols_cyclone),
                                                    contentDescription = "Root shell"
                                                )
                                            },
                                            text = {
                                                Text("Root Shell")
                                            },
                                            onClick = {
                                                shellEnvironment = ShellEnvironment.RootShell
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(30.dp)
        )

        Card(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(30.dp)
            ){
                items(
                    items = shellOutputList
                ){ shellResult ->
                    Text(shellResult.command)
                    if (shellResult.resulTag == ResultTag.RedLine){
                        Text(
                            shellResult.content,
                            color = Color(Color.RED)
                        )
                    }else{
                        Text(
                            shellResult.content,
                        )
                    }
                }

            }
        }
    }
}

@Composable
@PreviewLightDark
private fun GreetingScreenPreview(){
    Scaffold(
        topBar = {
            ShellScreenNecessaryComponents.ShellScreenScreenTopAppBar()
        }
    ) { contentPadding->
        ShellScreen(contentPaddingValues = contentPadding)
    }
}