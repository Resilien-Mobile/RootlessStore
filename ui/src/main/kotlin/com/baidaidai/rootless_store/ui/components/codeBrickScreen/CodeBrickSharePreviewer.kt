package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.ui.R

@Composable
fun CodeBrickSharePreviewer(
    modifier: Modifier = Modifier,
    codeBrickConfig: CodeBrickConfig,
    onShareButtonClick: (codeBrickConfig: CodeBrickConfig)-> Unit,
    onDismissClick: ()-> Unit,
) {

    val shareButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary
    )

    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(
                onClick = onDismissClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            IconButton(
                onClick = {
                    onShareButtonClick(codeBrickConfig)
                },
                colors = shareButtonColors,
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_ios_share),
                    contentDescription = stringResource(R.string.code_brick_screen_share_content_description)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _CodeBrickSharePreviewerPreview_() {
    CodeBrickSharePreviewer(
        modifier = Modifier.size(
            width = 200.dp,
            height = 180.dp
        ),
        codeBrickConfig = CodeBrickConfig(
            unixTimestamp = 0L,
            codeBrickTitle = "Reboot",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "while true; do\n    echo \"Rootless Store\"\n    sleep 1\ndone"
        ),
        onShareButtonClick = {},
        onDismissClick = {}
    )
}
