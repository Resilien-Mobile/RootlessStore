package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidaidai.rootless_store.components.homeScreen.HowToDevelopRootlessStorePlugin
import com.baidaidai.rootless_store.components.homeScreen.RootLessStoreVersionCheckerContainer
import com.baidaidai.rootless_store.components.homeScreen.RootlessStoreHosterStatusBoard
import com.baidaidai.rootless_store.domain.status.model.RootlessStoreHosterStatus
import com.baidaidai.rootless_store.ui.model.RootLessStoreHomeScreenViewModel

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onChipClick:()-> Unit,
    homeScreenViewModel: RootLessStoreHomeScreenViewModel = hiltViewModel()
){
    val memoryStatus by homeScreenViewModel.memoryStatus.collectAsState()
    val storageStatus by homeScreenViewModel.storageStatus.collectAsState()
    val pluginStatus by homeScreenViewModel.pluginStatus.collectAsState()
    val temperatureStatus by homeScreenViewModel.temperatureStatus.collectAsState()
    val seLinuxStatus by homeScreenViewModel.seLinuxStatus.collectAsState()
    val kernelStatus by homeScreenViewModel.kernelStatus.collectAsState()
    val androidAndAPIStatus by homeScreenViewModel.androidAndAPIStatus.collectAsState()
    val hosterOverallStatus by homeScreenViewModel.overallStatus.collectAsState()

    val rootlessStoreHosterStatus = RootlessStoreHosterStatus(
        hosterOverallStatus = hosterOverallStatus,
        osAndAPIVersion = androidAndAPIStatus,
        kernelVersion = kernelStatus,
        selinuxStatus = seLinuxStatus,
        pluginStatus = pluginStatus,
        memoryStatus = memoryStatus,
        storageStatus = storageStatus,
        tempStatus = temperatureStatus
    )



    val lifecycleOwner = LocalLifecycleOwner.current
    // A Listener about Maintaining OverallStatus fresh
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeScreenViewModel.refreshHosterOverallStatus()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Column(
        modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp)
    ) {
        /* Version */
        RootLessStoreVersionCheckerContainer()
        Spacer(
            modifier = Modifier
                .height(12.dp)
        )

        /* Hoster Status */
        RootlessStoreHosterStatusBoard(
            hosterStatus = rootlessStoreHosterStatus,
            onChipClick = onChipClick
        )
        Spacer(
            modifier = Modifier
                .height(12.dp)
        )

        /* How to Make Plugin */
        HowToDevelopRootlessStorePlugin()
    }
}