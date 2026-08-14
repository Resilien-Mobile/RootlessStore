package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.status.model.RootlessStoreHosterStatus
import com.baidaidai.rootless_store.ui.adaptive.RootlessStoreWindowSize
import com.baidaidai.rootless_store.ui.components.homeScreen.HomeScreenContextSwitchDialog
import com.baidaidai.rootless_store.ui.components.homeScreen.HomeScreenCpuInfoCard
import com.baidaidai.rootless_store.ui.components.homeScreen.HomeScreenNetDashboard
import com.baidaidai.rootless_store.ui.components.homeScreen.HosterStatusCircularProgressRow
import com.baidaidai.rootless_store.ui.components.homeScreen.HowToDevelopRootlessStorePlugin
import com.baidaidai.rootless_store.ui.components.homeScreen.RootLessStoreVersionCheckerContainer
import com.baidaidai.rootless_store.ui.components.homeScreen.RootLessStoreVersionTagContainer
import com.baidaidai.rootless_store.ui.components.homeScreen.RootlessStoreHosterStatusBoard
import com.baidaidai.rootless_store.ui.model.RootLessStoreHomeScreenViewModel

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    rootlessStoreWindowSize: RootlessStoreWindowSize,
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
    val dialogStats by homeScreenViewModel.dialogStatus.collectAsState()
    val latestVersionNumber by homeScreenViewModel.latestVersion.collectAsState()
    val cpuStatus by homeScreenViewModel.cpuStatus.collectAsState()
    val netStatus by homeScreenViewModel.netStatus.collectAsState()

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

    if (dialogStats){
        HomeScreenContextSwitchDialog(
            onDismissButtonClick = homeScreenViewModel::changeDialogStatus,
            homeScreenViewModel = homeScreenViewModel,
            onConfirmButtonClick = homeScreenViewModel::setExecuteContextPreference,
            onRevertButtonClick = homeScreenViewModel::revertExecuteContextPreference
        )
    }

    val modifier = if (rootlessStoreWindowSize == RootlessStoreWindowSize.Compact) {
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
    } else {
        Modifier
            .width(345.dp)
            .fillMaxHeight()
    }

    if(rootlessStoreWindowSize == RootlessStoreWindowSize.Compact){
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
        ) {
            item {

                /* Version Tag */
                RootLessStoreVersionTagContainer()
                Spacer(modifier = Modifier.height(12.dp))

                /* Version Checker */
                if (latestVersionNumber != null && latestVersionNumber != stringResource(R.string.app_version)) {
                    RootLessStoreVersionCheckerContainer(
                        latestVersionNumber = latestVersionNumber!!
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                /* Hoster Circular Progress */
                HosterStatusCircularProgressRow(
                    hosterStatus = rootlessStoreHosterStatus,
                    onChipClick = onChipClick,
                    onChipLongClick = homeScreenViewModel::changeDialogStatus
                )
                Spacer(modifier = Modifier.height(12.dp))

                /* Hoster Status */
                RootlessStoreHosterStatusBoard(
                    hosterStatus = rootlessStoreHosterStatus
                )
                Spacer(modifier = Modifier.height(12.dp))

                /* How to Make Plugin */
                HowToDevelopRootlessStorePlugin()

            }
        }
    }else{
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            item {
                Column(
                    modifier = modifier
                ) {
                    /* Version Tag */
                    RootLessStoreVersionTagContainer()
                    Spacer(modifier = Modifier.height(10.dp))

                    /* Version Checker */
                    if (latestVersionNumber != null && latestVersionNumber != stringResource(R.string.app_version)) {
                        RootLessStoreVersionCheckerContainer(
                            latestVersionNumber = latestVersionNumber!!
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    /* Hoster Circular Progress */
                    HosterStatusCircularProgressRow(
                        hosterStatus = rootlessStoreHosterStatus,
                        onChipClick = onChipClick,
                        onChipLongClick = homeScreenViewModel::changeDialogStatus
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    /* Hoster Status */
                    RootlessStoreHosterStatusBoard(
                        hosterStatus = rootlessStoreHosterStatus
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    /* How to Make Plugin */
                    HowToDevelopRootlessStorePlugin()
                }
            }
            item { Spacer(modifier = Modifier.width(10.dp)) }
            item {
                Column(
                    modifier = modifier
                ) {
                    HomeScreenCpuInfoCard(
                        cpuDashboardConfig = cpuStatus,
                        modifier = Modifier.height(280.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HomeScreenNetDashboard(
                        netDashboardConfig = netStatus
                    )
                }
            }
        }
    }
}