package com.baidaidai.rootless_store.components.homeScreen

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
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.ui.model.RootLessStoreHomeScreenViewModel
import androidx.compose.ui.graphics.*


private data class HomeScreenContextSwitchDialogSpec(
    val option: HosterOverallStatus,
    @DrawableRes val iconResource: Int,
    val content: String,
    val canUse: Boolean
)

@Composable
fun HomeScreenContextSwitchDialog(
    onDismissButtonClick: () -> Unit,
    onConfirmButtonClick: ()-> Unit,
    onRevertButtonClick:()-> Unit,
    homeScreenViewModel: RootLessStoreHomeScreenViewModel
){



    val adbStatus by homeScreenViewModel.adbStatus.collectAsState()
    val rootStatus by homeScreenViewModel.rootStatus.collectAsState()

    val currentSelected by homeScreenViewModel.currentExecuteContextSelected.collectAsState()

    val HomeScreenContextSwitchDialogRenderingList = listOf(
        HomeScreenContextSwitchDialogSpec(
            option = HosterOverallStatus.LIMITED,
            iconResource = R.drawable.material_symbols_disabled,
            content = "LIMITED",
            canUse = true
        ),
        HomeScreenContextSwitchDialogSpec(
            option = HosterOverallStatus.ADB,
            iconResource = R.drawable.material_symbols_adb,
            content = "ADB",
            canUse = adbStatus
        ),
        HomeScreenContextSwitchDialogSpec(
            option = HosterOverallStatus.ROOTD,
            iconResource = R.drawable.material_symbols_cyclone,
            content = "ROOT",
            canUse = rootStatus
        )
    )

    AlertDialog(
        onDismissRequest = onDismissButtonClick,
        confirmButton = {
            TextButton(
                onClick = onConfirmButtonClick
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onRevertButtonClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = androidx.compose.ui.graphics.Color.Red
                    )
                ) {
                    Text("revert")
                }
                TextButton(
                    onClick = onDismissButtonClick,
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
                HomeScreenContextSwitchDialogRenderingList.forEach { spec ->
                    HomeScreenContextSwitchDialogItem(
                        currentSelected = currentSelected.name,
                        onClick = {
                            homeScreenViewModel.setCurrentExecuteContextSelected(spec.option)
                        },
                        homeScreenContextSwitchDialogSpec = spec
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreenContextSwitchDialogItem(
    currentSelected: String,
    onClick: () -> Unit,
    homeScreenContextSwitchDialogSpec: HomeScreenContextSwitchDialogSpec
){
    ListItem(
        onClick = onClick,
        enabled = homeScreenContextSwitchDialogSpec.canUse,
        modifier = Modifier
            .fillMaxWidth(),
        leadingContent = {
            Icon(
                painter = painterResource(homeScreenContextSwitchDialogSpec.iconResource),
                contentDescription = null
            )
        },
        trailingContent = {
            RadioButton(
                enabled = homeScreenContextSwitchDialogSpec.canUse,
                selected = currentSelected == homeScreenContextSwitchDialogSpec.content,
                onClick = onClick,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = AlertDialogDefaults.containerColor,
            disabledContainerColor = AlertDialogDefaults.containerColor
        ),
    ){
        Text(homeScreenContextSwitchDialogSpec.content)
    }
}

//@Composable
//@PreviewLightDark
//private fun _HomeScreenContextSwitchDialogPreview_(){
//    HomeScreenContextSwitchDialog(
//        onConfirmButtonClick  = {},
//        onDismissButtonClick = {},
//        title = "Context Switch"
//    )
//}
