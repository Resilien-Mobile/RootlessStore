package com.baidaidai.rootless_store.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.components.shizukuAdbScreen.ShizukuAdbScreenNecessaryComponents.ShizukuAdbScreenActionCard
import com.baidaidai.rootless_store.components.shizukuAdbScreen.ShizukuAdbScreenNecessaryComponents.ShizukuAdbScreenModelSheet
import com.baidaidai.rootless_store.components.shizukuAdbScreen.ShizukuAdbScreenNecessaryComponents.ShizukuAdbScreenOverviewCard
import com.baidaidai.rootless_store.ui.model.RootlessStoreShizukuAdbScreenViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShizukuAdbScreen(
    contentPaddingValues: PaddingValues,
    shizukuAdbScreenViewModel: RootlessStoreShizukuAdbScreenViewModel,
){
    val shizukuActived by shizukuAdbScreenViewModel.shizukuActived.collectAsState()
    val endpointActived by shizukuAdbScreenViewModel.endpointActived.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    var sheetState by remember { mutableStateOf(false) }
    var remainderTime by remember { mutableIntStateOf(6) }


    LaunchedEffect(endpointActived) {
        if (endpointActived) {
            sheetState = true
            while (remainderTime > 0){
                delay(1000)
                remainderTime--
            }
            activity?.finish()
        }
    }

    if (sheetState){
        ShizukuAdbScreenModelSheet(
            remainderTime = remainderTime,
            onDismissRequest = { sheetState = false},
            onCloseButtonClick = { sheetState = false },
            onReturnButtonClick = { activity?.finish() }
        )
    }else{
        LazyColumn(
            modifier = Modifier
                .padding(contentPaddingValues)
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 15.dp)
        ) {
            item {
                ShizukuAdbScreenOverviewCard()
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearWavyProgressIndicator(
                        progress = {
                            if (endpointActived) {
                                1f
                            }else if (shizukuActived){
                                0.5f
                            }else{
                                0.05f
                            }
                        },
                        amplitude = {1f},
                        waveSpeed = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            item {
                ShizukuAdbScreenActionCard(
                    step = "Step 1",
                    title = "Request Shizuku Auth",
                    description = "Grant ADB authorization so Rootless Store can start the shell workflow and unlock the next step",
                    targetStatus = shizukuActived,
                    onClick = {
                        shizukuAdbScreenViewModel.activeShizuku()
                    }
                )
            }
            item {
                ShizukuAdbScreenActionCard(
                    step = "Step 2",
                    title = "Connect to Shizuku",
                    description = "After authorization is ready open Shizuku and enter the ADB shell session to finish setup and continue",
                    targetStatus = endpointActived,
                    onClick = {
                        shizukuAdbScreenViewModel.activeShizukuEndpoint()
                    }
                )
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//@PreviewLightDark
//private fun _ShizukuAdbScreenPreview_(){
//    RootlessStoreTheme {
//        Scaffold { contentPadding ->
//            ShizukuAdbScreen(contentPaddingValues = contentPadding)
//        }
//    }
//}
