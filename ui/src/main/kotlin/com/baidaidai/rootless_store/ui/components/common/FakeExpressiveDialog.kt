package com.baidaidai.rootless_store.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R

@Composable
fun FakeExpressiveDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    revertButton: @Composable (()-> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    enableSubTitle: Boolean = false,
    subtitle: @Composable (()-> Unit)? = null,
    enableContent: Boolean = false,
    content: @Composable (() -> Unit)? = null
){

    // Fake mask layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .wrapContentSize()
    ) {

        // Fake ExpressiveDialog Container
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(28.dp))
                .fillMaxWidth()
                .widthIn(
                    min = 320.dp,
                    max = 560.dp
                )
        ){

            // Fake ExpressiveDialog Content
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .padding(24.dp)
            ){

                // Icon
                if (icon != null){
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Box(modifier = Modifier.size(24.dp)) {
                            icon()
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Title
                if (title != null) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        title()
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Content
                Column(modifier = Modifier.fillMaxWidth()) {

                    if (enableSubTitle){
                        subtitle?.invoke()

                        if (enableContent){
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    content?.invoke()

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Button
                if (dismissButton != null || confirmButton != null || revertButton != null) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box() {
                            revertButton?.invoke()
                        }
                        Row{
                            if (dismissButton != null) {
                                dismissButton()
                            }

                            if (dismissButton != null && confirmButton != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            if (confirmButton != null) {
                                confirmButton()
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun SingletonTextButton(
    modifier: Modifier = Modifier,
    text: String = "Void",
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    enable: Boolean = true,
    onClick: ()-> Unit,
){
    Text(
        text = text,
        color = color,
        style = style,
        modifier = modifier
            .clickable(
                enabled = enable,
                onClick = onClick
            )
    )
}

@PreviewLightDark
@Composable
private fun _preview_() {
    FakeExpressiveDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {}
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {}
            ) {
                Text("Cancel")
            }
        },
        revertButton = {
            SingletonTextButton(
                text = "Revert",
                onClick = {}
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.material_symbols_warning),
                contentDescription = null
            )
        },
        title = {
            Text(
                text = "Invalid CodeBrick json.",
                style = MaterialTheme.typography.titleLarge
            )
        },
        enableSubTitle = true,
        subtitle = {
            Text("Rootless Store could not parse clipboard content as a CodeBrick JSON payload.")
        },
        enableContent = true,
        content = {
            Text("Please check whether required fields and enum values match the CodeBrick schema.")
        }
    )
}
