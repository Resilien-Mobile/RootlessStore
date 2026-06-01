package com.baidaidai.rootless_store.components.marketScreen

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginType
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus

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

    AssistChip(
        enabled = false,
        onClick = {},
        label = {
            Text(pluginType.name)
        },
        leadingIcon = {
            Icon(
                painter = leadingIconPainter,
                contentDescription = pluginType.name
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

    AssistChip(
        enabled = false,
        onClick = {},
        label = {
            Text(pluginRunModel.name)
        },
        leadingIcon = {
            Icon(
                painter = leadingIconPainter,
                contentDescription = pluginRunModel.name
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
    pluginRequired: HosterOverallStatus,
    modifier: Modifier = Modifier,
){
    val leadingIconPainter = when(pluginRequired){
        HosterOverallStatus.LIMITED -> {
            painterResource(R.drawable.material_symbols_disabled)
        }

        HosterOverallStatus.PERMISSIVE -> {
            painterResource(R.drawable.material_symbols_warning_24px)
        }

        HosterOverallStatus.ADB -> {
            painterResource(R.drawable.material_symbols_adb)
        }

        HosterOverallStatus.ROOTD -> {
            painterResource(R.drawable.material_symbols_cyclone)
        }
    }
    val primaryColor = when(pluginRequired){
        HosterOverallStatus.LIMITED -> {
            Color(0xFF8E8E93)
        }

        HosterOverallStatus.PERMISSIVE -> {
            Color(0xFF8E8E93)
        }

        HosterOverallStatus.ADB -> {
            Color(0xFF0A84FF)
        }

        HosterOverallStatus.ROOTD -> {
            Color(0xFFFF3B30)
        }
    }

    AssistChip(
        enabled = false,
        onClick = {},
        label = {
            Text(pluginRequired.name)
        },
        leadingIcon = {
            Icon(
                painter = leadingIconPainter,
                contentDescription = pluginRequired.name
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