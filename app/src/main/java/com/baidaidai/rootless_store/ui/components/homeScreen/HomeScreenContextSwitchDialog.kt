package com.baidaidai.rootless_store.ui.components.homeScreen

import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.ui.model.RootlessStoreHomeScreenViewModel
import androidx.compose.ui.graphics.*


private data class ExecutionContextOption(
    val executionContext: ExecutionContext,
    @DrawableRes val iconResource: Int,
    val label: String,
    val isEnabled: Boolean
)

@Composable
fun HomeScreenContextSwitchDialog(
    onDismissRequest: () -> Unit,
    onApplyExecutionContext: () -> Unit,
    onResetExecutionContextPreference: () -> Unit,
    homeScreenViewModel: RootlessStoreHomeScreenViewModel
){



    val isAdbShellAvailable by homeScreenViewModel.isAdbShellAvailable.collectAsState()
    val isRootShellAvailable by homeScreenViewModel.isRootShellAvailable.collectAsState()

    val selectedExecutionContext by homeScreenViewModel.selectedExecutionContext.collectAsState()

    val executionContextOptions = listOf(
        ExecutionContextOption(
            executionContext = ExecutionContext.LIMITED,
            iconResource = R.drawable.material_symbols_disabled,
            label = "LIMITED",
            isEnabled = true
        ),
        ExecutionContextOption(
            executionContext = ExecutionContext.ADB,
            iconResource = R.drawable.material_symbols_adb,
            label = "ADB",
            isEnabled = isAdbShellAvailable
        ),
        ExecutionContextOption(
            executionContext = ExecutionContext.ROOTD,
            iconResource = R.drawable.material_symbols_cyclone,
            label = "ROOT",
            isEnabled = isRootShellAvailable
        )
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onApplyExecutionContext
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onResetExecutionContextPreference,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = androidx.compose.ui.graphics.Color.Red
                    )
                ) {
                    Text("revert")
                }
                TextButton(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }
            }
        },
        title = {
            Text("Context Switcher")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                executionContextOptions.forEach { executionContextOption ->
                    HomeScreenContextSwitchDialogItem(
                        selectedExecutionContext = selectedExecutionContext,
                        onClick = {
                            homeScreenViewModel.selectExecutionContext(executionContextOption.executionContext)
                        },
                        executionContextOption = executionContextOption
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreenContextSwitchDialogItem(
    selectedExecutionContext: ExecutionContext,
    onClick: () -> Unit,
    executionContextOption: ExecutionContextOption
){
    ListItem(
        onClick = onClick,
        enabled = executionContextOption.isEnabled,
        modifier = Modifier
            .fillMaxWidth(),
        leadingContent = {
            Icon(
                painter = painterResource(executionContextOption.iconResource),
                contentDescription = null
            )
        },
        trailingContent = {
            RadioButton(
                enabled = executionContextOption.isEnabled,
                selected = selectedExecutionContext == executionContextOption.executionContext,
                onClick = onClick,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = AlertDialogDefaults.containerColor,
            disabledContainerColor = AlertDialogDefaults.containerColor
        ),
    ){
        Text(executionContextOption.label)
    }
}
