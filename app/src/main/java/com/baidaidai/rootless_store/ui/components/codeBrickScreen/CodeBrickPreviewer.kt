package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.R

@Composable
fun CodeBrickPreviewer(
    modifier: Modifier = Modifier,
    codeBrickConfig: CodeBrickConfig,
    onActionButtonClick: (codeBrickConfig: CodeBrickConfig)-> Unit,
    onSettingButtonClick: (codeBrickConfig: CodeBrickConfig)-> Unit,
    onDeleteButtonClick: (codeBrickConfig: CodeBrickConfig)-> Unit
){

    // TODO("Add Carry out opportunity assessment")
    // Will Change It Default Color : Green / Error / Disabled
    val actionButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
    )

    val modifierButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
    )

    val deleteButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
    )

    Box{
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Data Area
            Column {
                Text(
                    text = codeBrickConfig.codeBrickTitle,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(16.dp)
                ) {
                    Text(
                        text = codeBrickConfig.codeBrickContent,
                        softWrap = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Area
            Row(
                modifier = Modifier
                    .widthIn(200.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    colors = actionButtonColors,
                    modifier = Modifier.weight(1.5f),
                    onClick = {onActionButtonClick(codeBrickConfig)}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_play_arrow),
                        contentDescription = stringResource(R.string.code_brick_screen_previewer_play_content_description)
                    )
                }
                IconButton(
                    colors = modifierButtonColors,
                    modifier = Modifier.weight(1f),
                    onClick = {onSettingButtonClick(codeBrickConfig)}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_settings),
                        contentDescription = stringResource(R.string.code_brick_screen_previewer_setting_content_description)
                    )
                }
                IconButton(
                    colors = deleteButtonColors,
                    modifier = Modifier.weight(1f),
                    onClick = { onDeleteButtonClick(codeBrickConfig) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_delete),
                        contentDescription = stringResource(R.string.code_brick_screen_previewer_delete_content_description)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _preview_(){
    val codeBrickConfig = CodeBrickConfig(
        unixTimeStamp = 0L,
        codeBrickTitle = "Test Brick",
        codeBrickEnvironment = HosterOverallStatus.LIMITED,
        codeBrickContent = "whoami"
    )

    CodeBrickPreviewer(
        modifier = Modifier.width(200.dp),
        codeBrickConfig = codeBrickConfig,
        onActionButtonClick = {},
        onDeleteButtonClick = {},
        onSettingButtonClick = {}
    )
}
