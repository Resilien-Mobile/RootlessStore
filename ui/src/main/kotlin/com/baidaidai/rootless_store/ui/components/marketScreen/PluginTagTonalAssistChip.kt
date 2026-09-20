package com.baidaidai.rootless_store.ui.components.marketScreen

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginType
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

@Composable
fun PluginTagTonalAssistChip(
    pluginType: PluginType,
    modifier: Modifier = Modifier,
){

    val leadingIconPainter = when(pluginType){
        PluginType.Client -> {
            painterResource(R.drawable.outline_extension_24)
        }

        PluginType.Environment ->{
            painterResource(R.drawable.material_symbols_svideo_)
        }
    }
    val primaryColor = Color(0xFFFFC400)
    val labelResource = when(pluginType){
        PluginType.Client -> R.string.plugin_type_client_label
        PluginType.Environment -> R.string.plugin_type_environment_label
    }

    AssistChip(
        enabled = false,
        onClick = {},
        label = {
            Text(stringResource(labelResource))
        },
        leadingIcon = {
            Icon(
                painter = leadingIconPainter,
                contentDescription = stringResource(labelResource)
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = primaryColor.copy(alpha = 0.12f),
            labelColor = primaryColor,
            leadingIconContentColor = primaryColor,
            trailingIconContentColor = primaryColor,
            disabledContainerColor = primaryColor.copy(alpha = 0.12f),
            disabledLabelColor = primaryColor,
            disabledLeadingIconContentColor = primaryColor,
            disabledTrailingIconContentColor = primaryColor,
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = primaryColor
        )
    )
}

@Composable
fun PluginTagTonalAssistChip(
    pluginRunModel: PluginRunModel,
    modifier: Modifier = Modifier,
){
    val leadingIconPainter = when(pluginRunModel){
        PluginRunModel.OneTime -> {
            painterResource(R.drawable.material_symbols_falling)
        }

        PluginRunModel.Daemon -> {
            painterResource(R.drawable.material_symbols_partly_cloudy_night)
        }
    }
    val primaryColor = when(pluginRunModel){
        PluginRunModel.OneTime -> {
            Color(0xFF34C759)
        }

        PluginRunModel.Daemon -> {
            Color(0xFF30D158)
        }
    }
    val labelResource = when(pluginRunModel){
        PluginRunModel.OneTime -> R.string.plugin_run_model_one_time_label
        PluginRunModel.Daemon -> R.string.plugin_run_model_daemon_label
    }

    AssistChip(
        enabled = false,
        onClick = {},
        label = {
            Text(stringResource(labelResource))
        },
        leadingIcon = {
            Icon(
                painter = leadingIconPainter,
                contentDescription = stringResource(labelResource)
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = primaryColor.copy(alpha = 0.12f),
            labelColor = primaryColor,
            leadingIconContentColor = primaryColor,
            trailingIconContentColor = primaryColor,
            disabledContainerColor = primaryColor.copy(alpha = 0.12f),
            disabledLabelColor = primaryColor,
            disabledLeadingIconContentColor = primaryColor,
            disabledTrailingIconContentColor = primaryColor,
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = primaryColor
        )
    )
}


@Composable
fun PluginTagTonalAssistChip(
    pluginRequired: ExecutionContext,
    modifier: Modifier = Modifier,
){
    val leadingIconPainter = when(pluginRequired){
        ExecutionContext.LIMITED -> {
            painterResource(R.drawable.material_symbols_disabled)
        }

        ExecutionContext.PERMISSIVE -> {
            painterResource(R.drawable.material_symbols_warning)
        }

        ExecutionContext.ADB -> {
            painterResource(R.drawable.material_symbols_adb)
        }

        ExecutionContext.ROOTD -> {
            painterResource(R.drawable.material_symbols_cyclone)
        }
    }
    val primaryColor = when(pluginRequired){
        ExecutionContext.LIMITED -> {
            Color(0xFF8E8E93)
        }

        ExecutionContext.PERMISSIVE -> {
            Color(0xFF8E8E93)
        }

        ExecutionContext.ADB -> {
            Color(0xFF0A84FF)
        }

        ExecutionContext.ROOTD -> {
            Color(0xFFFF3B30)
        }
    }
    val labelResource = when(pluginRequired){
        ExecutionContext.LIMITED -> R.string.execution_context_app_shell_label
        ExecutionContext.PERMISSIVE -> R.string.execution_context_permissive_label
        ExecutionContext.ADB -> R.string.execution_context_adb_shell_label
        ExecutionContext.ROOTD -> R.string.execution_context_root_shell_label
    }

    AssistChip(
        enabled = false,
        onClick = {},
        label = {
            Text(stringResource(labelResource))
        },
        leadingIcon = {
            Icon(
                painter = leadingIconPainter,
                contentDescription = stringResource(labelResource)
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = primaryColor.copy(alpha = 0.12f),
            labelColor = primaryColor,
            leadingIconContentColor = primaryColor,
            trailingIconContentColor = primaryColor,
            disabledContainerColor = primaryColor.copy(alpha = 0.12f),
            disabledLabelColor = primaryColor,
            disabledLeadingIconContentColor = primaryColor,
            disabledTrailingIconContentColor = primaryColor,
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = primaryColor
        )
    )
}

@Composable
@PreviewLightDark
private fun _PluginTagTonalAssistChipPreview_(){
    PluginTagTonalAssistChip(
        pluginRunModel = PluginRunModel.Daemon
    )
}
