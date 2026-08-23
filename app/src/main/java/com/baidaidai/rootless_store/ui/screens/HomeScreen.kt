package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import com.baidaidai.rootless_store.ui.components.homeScreen.RootlessStoreVersionCheckerContainer
import com.baidaidai.rootless_store.ui.components.homeScreen.RootlessStoreVersionTagContainer
import com.baidaidai.rootless_store.ui.components.homeScreen.RootlessStoreHosterStatusBoard
import com.baidaidai.rootless_store.ui.layout.homeScreen.HomeScreenExpandedLayout
import com.baidaidai.rootless_store.ui.model.RootlessStoreHomeScreenViewModel

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    rootlessStoreHeightWindowSize: RootlessStoreWindowSize,
    rootlessStoreWidthWindowSize: RootlessStoreWindowSize,
    onChipClick:()-> Unit,
    homeScreenViewModel: RootlessStoreHomeScreenViewModel = hiltViewModel()
){
    val memoryStatus by homeScreenViewModel.memoryStatus.collectAsState()
    val storageStatus by homeScreenViewModel.storageStatus.collectAsState()
    val pluginStatus by homeScreenViewModel.pluginStatus.collectAsState()
    val temperatureStatus by homeScreenViewModel.temperatureStatus.collectAsState()
    val seLinuxStatus by homeScreenViewModel.seLinuxStatus.collectAsState()
    val kernelStatus by homeScreenViewModel.kernelStatus.collectAsState()
    val androidAndApiStatus by homeScreenViewModel.androidAndApiStatus.collectAsState()
    val hosterOverallStatus by homeScreenViewModel.overallStatus.collectAsState()
    val dialogStats by homeScreenViewModel.dialogStatus.collectAsState()
    val latestVersionNumber by homeScreenViewModel.latestVersion.collectAsState()
    val cpuStatus by homeScreenViewModel.cpuStatus.collectAsState()
    val netStatus by homeScreenViewModel.netStatus.collectAsState()
    val appVersion = stringResource(R.string.app_version)

    val rootlessStoreHosterStatus = RootlessStoreHosterStatus(
        hosterOverallStatus = hosterOverallStatus,
        osAndApiVersion = androidAndApiStatus,
        kernelVersion = kernelStatus,
        seLinuxStatus = seLinuxStatus,
        pluginStatus = pluginStatus,
        memoryStatus = memoryStatus,
        storageStatus = storageStatus,
        tempStatus = temperatureStatus
    )

    if (dialogStats){
        HomeScreenContextSwitchDialog(
            onDismissButtonClick = homeScreenViewModel::changeDialogStatus,
            homeScreenViewModel = homeScreenViewModel,
            onConfirmButtonClick = homeScreenViewModel::setExecutionContextPreference,
            onRevertButtonClick = homeScreenViewModel::revertExecutionContextPreference
        )
    }

    if(rootlessStoreWidthWindowSize == RootlessStoreWindowSize.Compact){
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                /* Version Tag */
                RootlessStoreVersionTagContainer()
            }

            if (latestVersionNumber != null && latestVersionNumber != appVersion) {
                item {
                    /* Version Checker */
                    RootlessStoreVersionCheckerContainer(
                        latestVersionNumber = latestVersionNumber!!
                    )
                }
            }

            item {
                /* Hoster Circular Progress */
                HosterStatusCircularProgressRow(
                    hosterStatus = rootlessStoreHosterStatus,
                    onChipClick = onChipClick,
                    onChipLongClick = homeScreenViewModel::changeDialogStatus
                )
            }

            item {
                /* Hoster Status */
                RootlessStoreHosterStatusBoard(
                    hosterStatus = rootlessStoreHosterStatus
                )
            }

            item {
                /* How to Make Plugin */
                HowToDevelopRootlessStorePlugin()
            }
        }
    }else{
        HomeScreenExpandedLayout(
            preferWidth = 345.dp,
            modifier = Modifier
                .fillMaxHeight()
                .padding(contentPadding)
                .horizontalScroll(rememberScrollState())
        ){

            // Version Tag && Info Flag
            RootlessStoreVersionTagContainer(Modifier.width(preferWidth))

            if (latestVersionNumber != null && latestVersionNumber != appVersion && rootlessStoreHeightWindowSize != RootlessStoreWindowSize.Compact) {
                RootlessStoreVersionCheckerContainer(
                    latestVersionNumber = latestVersionNumber!!,
                    modifier = getBasicWidthModifier()
                )
            }

            /* Hoster Circular Progress */
            HosterStatusCircularProgressRow(
                hosterStatus = rootlessStoreHosterStatus,
                onChipClick = onChipClick,
                onChipLongClick = homeScreenViewModel::changeDialogStatus,
                modifier = getBasicWidthModifier()
            )

            /* Version Checker */
            // 如果height紧凑，则可用此布局，反之不可使用
            if (latestVersionNumber != null && latestVersionNumber != appVersion && rootlessStoreHeightWindowSize == RootlessStoreWindowSize.Compact) {
                RootlessStoreVersionCheckerContainer(
                    latestVersionNumber = latestVersionNumber!!,
                    modifier = getBasicWidthModifier()
                )
            }
            // 如果height紧凑，则可用此布局，反之不可使用
            if (rootlessStoreHeightWindowSize == RootlessStoreWindowSize.Compact){
                /* How to Make Plugin */
                HowToDevelopRootlessStorePlugin(getBasicWidthModifier())
            }

            /* Hoster Status */
            RootlessStoreHosterStatusBoard(
                hosterStatus = rootlessStoreHosterStatus,
                modifier = getBasicWidthModifier()
            )

            if (rootlessStoreHeightWindowSize != RootlessStoreWindowSize.Compact){
                /* How to Make Plugin */
                HowToDevelopRootlessStorePlugin(getBasicWidthModifier())
            }


            HomeScreenCpuInfoCard(
                cpuDashboardConfig = cpuStatus,
                modifier = getBasicWidthModifier(Modifier.height(280.dp))
            )

            HomeScreenNetDashboard(
                netDashboardConfig = netStatus,
                rootlessStoreHeightWindowSize = rootlessStoreHeightWindowSize,
                modifier = if (rootlessStoreHeightWindowSize == RootlessStoreWindowSize.Compact){
                    Modifier.fillMaxHeight()
                }else{
                    getBasicWidthModifier()
                }
            )

        }
    }
}
