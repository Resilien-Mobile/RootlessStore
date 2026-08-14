package com.baidaidai.rootless_store.ui.components.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenCpuInfoCell(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
){
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
        Text(value)
    }

}

@PreviewLightDark
@Composable
private fun _preview_() {
    HomeScreenCpuInfoCell(
        title = "USER",
        value = 64.toString(),
        modifier = Modifier
            .height(50.dp)
    )
}