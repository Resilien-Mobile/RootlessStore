package com.baidaidai.rootless_store.ui.screens

import android.graphics.Color
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantEngineStatus
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResultTag
import com.baidaidai.rootless_store.domain.shell.model.ShellCommand
import com.baidaidai.rootless_store.domain.shell.model.ShellEnvironment
import com.baidaidai.rootless_store.ui.components.common.ExpressiveDialog
import com.baidaidai.rootless_store.ui.model.RootlessStoreShellScreenViewModel
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import com.baidaidai.rootless_store.ui.components.common.HorizontalSpacer
import com.baidaidai.rootless_store.ui.components.common.VerticalSpacer
import com.baidaidai.rootless_store.ui.components.pluginScreen.ColorfulBadge
import com.baidaidai.rootless_store.ui.theme.ColorFactory
import com.baidaidai.rootless_store.ui.theme.SuccessColorSet
import com.baidaidai.rootless_store.ui.theme.getListItemColors

@Composable
fun ShellScreen(
    contentPaddingValues: PaddingValues,
    shellScreenViewModel: RootlessStoreShellScreenViewModel,
    lazyColumnState: LazyListState
){
    var commandContent by remember { mutableStateOf("") }
    var shellEnvironment by remember { mutableStateOf(ShellEnvironment.AppShell) }
    var isEnvironmentMenuExpanded by remember { mutableStateOf(false) }
    var shellError by remember { mutableStateOf<AssistantError?>(null) }

    val shellOutputs by shellScreenViewModel.shellOutputs.collectAsState()
    val isRootShellAvailable by shellScreenViewModel.isRootShellAvailable.collectAsState()
    val isAdbShellAvailable by shellScreenViewModel.isAdbShellAvailable.collectAsState()
    val shellContextPreferences by shellScreenViewModel.shellContextPreferences.collectAsState()
    val shellScreenUiState by shellScreenViewModel.shellScreenUiState.collectAsState()
    val assistantEngineStatus by shellScreenViewModel.assistantEngineStatus.collectAsState()

    LaunchedEffect(shellOutputs.size) {
        if (shellOutputs.isNotEmpty()) {
            lazyColumnState.scrollToItem(shellOutputs.lastIndex)
        }
    }
    LaunchedEffect(Unit) {
        shellScreenViewModel.resolvedMaskedCommand.collect { resolvedCommand ->
            commandContent = resolvedCommand
        }
    }
    LaunchedEffect(Unit) {
        shellScreenViewModel.shellError.collect { error ->
            shellError = error
        }
    }

    shellError?.let { error ->

        ExpressiveDialog(
            onDismissRequest = {
                shellError = null
            },
            confirmButton = {
                Button(
                    onClick = {
                        shellError = null
                    }
                ) {
                    Text(stringResource(R.string.start_screen_error_dialog_ok_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        shellError = null
                        shellScreenViewModel.switchEngineBoardState()
                    }
                ) {
                    Text(stringResource(R.string.shell_screen_assistant_error_check_button))
                }
            },
            icon = {
                val chaosRotation = remember { Animatable(0f) }

                LaunchedEffect(Unit) {
                    val rotationSequence = listOf(
                        35f to FastOutSlowInEasing,
                        -70f to FastOutSlowInEasing,
                        120f to FastOutLinearInEasing,
                        -45f to LinearOutSlowInEasing,
                        200f to FastOutSlowInEasing,
                        85f to FastOutLinearInEasing,
                        -160f to FastOutSlowInEasing,
                        25f to LinearOutSlowInEasing,
                        -110f to FastOutSlowInEasing,
                        180f to FastOutLinearInEasing,
                        -60f to LinearOutSlowInEasing,
                        40f to FastOutSlowInEasing
                    )
                    var cumulativeRotation = 0f

                    while (true) {
                        rotationSequence.forEach { (rotationDelta, easing) ->
                            cumulativeRotation += rotationDelta
                            chaosRotation.animateTo(
                                targetValue = cumulativeRotation,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = easing
                                )
                            )
                        }

                        cumulativeRotation %= 360f
                        chaosRotation.snapTo(cumulativeRotation)
                    }
                }

                Icon(
                    painter = painterResource(R.drawable.apple_intelligence),
                    contentDescription = stringResource(
                        R.string.start_screen_error_dialog_warning_icon_content_description
                    ),
                    modifier = Modifier.rotate(chaosRotation.value)
                )
            },
            title = {
                Text(error.errorMessage)
            },
            enableSubTitle = !error.errorCompanion.isNullOrEmpty(),
            subtitle = error.errorCompanion?.takeIf(String::isNotEmpty)?.let { companion ->
                {
                    Text(
                        text = companion,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            enableContent = !error.errorCause.isNullOrEmpty(),
            content = error.errorCause?.takeIf(String::isNotEmpty)?.let { cause ->
                {
                    Text(
                        text = cause,
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        )
    }

    if (shellScreenUiState.isEngineBoardVisible) {
        ExpressiveDialog(
            icon = {
                val assistantCoreIconRotationTransition = rememberInfiniteTransition(
                    label = "assistantCoreIconRotation"
                )
                val assistantCoreIconRotation by assistantCoreIconRotationTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 2400,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "assistantCoreIconRotation"
                )

                Icon(
                    painter = painterResource(R.drawable.apple_intelligence),
                    contentDescription = stringResource(
                        R.string.start_screen_error_dialog_warning_icon_content_description
                    ),
                    modifier = Modifier.rotate(
                        if (assistantEngineStatus == AssistantEngineStatus.Working) {
                            assistantCoreIconRotation
                        } else {
                            0f
                        }
                    )
                )
            },
            onDismissRequest = shellScreenViewModel::switchEngineBoardState,
            confirmButton = {
                Button(
                    onClick = shellScreenViewModel::switchEngineBoardState
                ) {
                    Text(stringResource(R.string.start_screen_error_dialog_ok_button))
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(
                        text = stringResource(R.string.shell_screen_assistant_engine_board_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    HorizontalSpacer( 12.dp)

                    if (assistantEngineStatus == AssistantEngineStatus.Working) {
                        ColorfulBadge(
                            backgroundColor = ColorFactory(SuccessColorSet).color,
                            contentColor = ColorFactory(SuccessColorSet).onColor,
                            content = {
                                Text(
                                    text = stringResource(R.string.shell_screen_assistant_engine_status_working),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    } else {
                        ColorfulBadge(
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            content = {
                                Text(
                                    text = stringResource(R.string.shell_screen_assistant_engine_status_released),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }
            },
            content = {
                Column(
                   modifier = Modifier
                       .clip(
                           shape = RoundedCornerShape(16.dp)
                       )
                ) {
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = shellScreenViewModel::initializeAssistantEngine
                        ),
                        colors = getListItemColors(),
                        headlineContent = {
                            Text(stringResource(R.string.shell_screen_assistant_engine_reload_action_title))
                        },
                        supportingContent = {
                            Text(stringResource(R.string.shell_screen_assistant_engine_reload_action_supporting))
                        }
                    )

                    VerticalSpacer(
                        height = 2.dp
                    )

                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = shellScreenViewModel::findAssistantModelFile
                        ),
                        colors = getListItemColors(),
                        headlineContent = {
                            Text(stringResource(R.string.shell_screen_assistant_model_status_action_title))
                        },
                        supportingContent = {
                            Text(stringResource(R.string.shell_screen_assistant_model_status_action_supporting))
                        }
                    )
                }
            }
        )
    }

    val shellEnvironmentSymbol = remember(shellEnvironment){
        when(shellEnvironment){
            ShellEnvironment.AppShell, ShellEnvironment.AdbShell -> "~"
            ShellEnvironment.RootShell -> "#"
        }
    }

    Column(
        modifier = Modifier
            .padding(contentPaddingValues)
            .padding(vertical = 15.dp, horizontal = 15.dp)
            .fillMaxSize()
    ) {

        val expandedTrailingButtonContentPadding = PaddingValues(start = 15.dp, end = 15.dp)
        val collapsedTrailingButtonContentPadding = PaddingValues(start = 13.dp, end = 17.dp)
        val collapsedTrailingButtonShape = SplitButtonDefaults.trailingButtonShapesFor(56.dp).shape

        val trailingButtonContentPadding = remember(isEnvironmentMenuExpanded) {
            if (isEnvironmentMenuExpanded){
                expandedTrailingButtonContentPadding
            }else{
                collapsedTrailingButtonContentPadding
            }
        }

        val trailingButtonShape = remember(isEnvironmentMenuExpanded) {
            if (isEnvironmentMenuExpanded){
                CircleShape
            }else{
                collapsedTrailingButtonShape
            }
        }

        val shellCommand = remember(key1 = commandContent, key2 = shellEnvironment) {
            ShellCommand(shellEnvironment, commandContent = commandContent)
        }

        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            Column{
                OutlinedTextField(
                    leadingIcon = {
                        Text(shellEnvironmentSymbol)
                    },
                    trailingIcon = {
                        IconButton(
                            enabled = shellScreenUiState.isTextFieldEnabled,
                            onClick = {
                                commandContent = ""
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_close_24),
                                contentDescription = stringResource(R.string.shell_screen_command_input_delete_content_description)
                            )
                        }
                    },
                    value = if (shellScreenUiState.isTextFieldEnabled) {
                        commandContent
                    } else {
                        stringResource(R.string.shell_screen_assistant_command_resolving_placeholder)
                    },
                    onValueChange = {
                        commandContent = it
                    },
                    enabled = shellScreenUiState.isTextFieldEnabled,
                    maxLines = 1,
                    label = {
                        Text(stringResource(R.string.shell_screen_command_input_label))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ){
                    when(shellEnvironment){
                        ShellEnvironment.AdbShell -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            ){
                                Text(stringResource(R.string.shell_screen_enable_run_as_label))
                                Checkbox(
                                    checked = shellContextPreferences.shouldJumpToDirectory,
                                    onCheckedChange = shellScreenViewModel::setDirectoryJumpEnabled
                                )
                            }
                        }
                        else -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                            ){
                                Text(stringResource(R.string.shell_screen_jump_to_directory_label))
                                Checkbox(
                                    checked = shellContextPreferences.shouldJumpToDirectory,
                                    onCheckedChange = shellScreenViewModel::setDirectoryJumpEnabled
                                )
                            }
                        }
                    }
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        SplitButtonLayout(
                            leadingButton = {
                                Surface(
                                    modifier = Modifier
                                        .height(56.dp)
                                        .combinedClickable(
                                            role = Role.Button,
                                            onClick = {
                                                // 普通点击
                                                shellScreenViewModel.executeCommand(shellCommand)
                                            },
                                            onLongClick = {
                                                // 长按
                                                shellScreenViewModel.resolveMaskedCommand(shellCommand.commandContent)
                                            }
                                        ),
                                    shape = SplitButtonDefaults.leadingButtonShapesFor(
                                        SplitButtonDefaults.MediumContainerHeight
                                    ).shape,
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.terminal_24px),
                                            contentDescription = null
                                        )
                                        Spacer(Modifier.size(8.dp))
                                        Text(stringResource(R.string.shell_screen_run_button_label))
                                    }
                                }
                            },
                            trailingButton = {
                                Column{
                                    Button(
                                        onClick = {
                                            isEnvironmentMenuExpanded = !isEnvironmentMenuExpanded
                                        },
                                        shape = trailingButtonShape,
                                        contentPadding = trailingButtonContentPadding,
                                        modifier = Modifier
                                            .height(56.dp)
                                    ){
                                        Icon(
                                            painterResource(R.drawable.material_symbols_keyboard_arrow_down_icon),
                                            contentDescription = stringResource(R.string.shell_screen_environment_menu_expand_content_description),
                                            modifier = Modifier
                                                .size(26.dp)
                                                .rotate(if (isEnvironmentMenuExpanded) 0f else -90f )
                                        )
                                    }

                                    DropdownMenuPopup(
                                        expanded = isEnvironmentMenuExpanded,
                                        onDismissRequest = { isEnvironmentMenuExpanded = !isEnvironmentMenuExpanded},
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
                                                        painterResource(R.drawable.material_symbols_applications),
                                                        contentDescription = stringResource(R.string.execution_context_app_shell_label)
                                                    )
                                                },
                                                text = {
                                                    Text(stringResource(R.string.execution_context_app_shell_label))
                                                },
                                                onClick = {
                                                    shellEnvironment = ShellEnvironment.AppShell
                                                },
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            DropdownMenuItem(
                                                enabled = isAdbShellAvailable,
                                                selected = shellEnvironment == ShellEnvironment.AdbShell,
                                                shapes = MenuDefaults.itemShape(2,4),
                                                leadingIcon = {
                                                    Icon(
                                                        painterResource(R.drawable.material_symbols_adb),
                                                        contentDescription = stringResource(R.string.execution_context_adb_shell_label)
                                                    )
                                                },
                                                text = {
                                                    Text(stringResource(R.string.execution_context_adb_shell_label))
                                                },
                                                onClick = {
                                                    shellEnvironment = ShellEnvironment.AdbShell
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            DropdownMenuItem(
                                                enabled = isRootShellAvailable,
                                                selected = shellEnvironment == ShellEnvironment.RootShell,
                                                shapes = MenuDefaults.itemShape(3,4),
                                                leadingIcon = {
                                                    Icon(
                                                        painterResource(R.drawable.material_symbols_cyclone),
                                                        contentDescription = stringResource(R.string.execution_context_root_shell_label)
                                                    )
                                                },
                                                text = {
                                                    Text(stringResource(R.string.execution_context_root_shell_label))
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
                state = lazyColumnState,
                modifier = Modifier
                    .padding(horizontal = 25.dp, vertical = 15.dp)
            ){
                items(
                    items = shellOutputs
                ){ shellResult ->
                    if(shellResult.command != null){
                        Text(shellResult.command.toString())
                    }

                    if (shellResult.resultTag == ExecutionResultTag.Error){
                        Text(
                            shellResult.output,
                            color = Color(Color.RED)
                        )
                    }else{
                        Text(
                            shellResult.output,
                        )
                    }
                }

            }
        }
    }
}
