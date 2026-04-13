package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baidaidai.rootless_store.ShizukuActivity
import com.baidaidai.rootless_store.components.executeScreen.executeScreenNecessaryComponents
import com.baidaidai.rootless_store.components.marketScreen.MarketScreenNecessaryComponents
import com.baidaidai.rootless_store.components.pluginsScreen.PluginScreenNecessaryComponents
import com.baidaidai.rootless_store.components.shellScreen.ShellScreenNecessaryComponents
import com.baidaidai.rootless_store.components.sourcesScreen.SourcesScreenNecessaryComponents
import com.baidaidai.rootless_store.components.startScreen.StartScreenErrorDialog
import com.baidaidai.rootless_store.components.startScreen.StartScreenRepositoryDialog
import com.baidaidai.rootless_store.components.startScreen.StartScreenNecessaryComponents
import com.baidaidai.rootless_store.domain.error.RootlessStoreError
import com.baidaidai.rootless_store.ui.model.RootLessStoreExecuteScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreMarketScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStorePluginScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreShellScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.theme.RootlessStoreTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootlessStoreStartScreenContainer(
    pluginScreenViewModel: RootLessStorePluginScreenViewModel = hiltViewModel(),
    sourceScreenViewModel: RootLessStoreSourceScreenViewModel = hiltViewModel(),
    fileIntentUri:Uri?,
    onHandlerEnded:()-> Unit
){
    // VM & VM Data
    val marketScreenViewModel = hiltViewModel<RootLessStoreMarketScreenViewModel>()
    val executeScreenViewModel = hiltViewModel<RootLessStoreExecuteScreenViewModel>()
    val shellScreenViewModel = hiltViewModel<RootLessStoreShellScreenViewModel>()
    val pluginInfoCount by pluginScreenViewModel.pluginInfoCount.collectAsState()
    val sourceCount by sourceScreenViewModel.sourceCount.collectAsState()

    // Navigation
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route ?: "HomeScreen"
    val currentPluginSource by marketScreenViewModel.currentPluginSource.collectAsState()

    // Define the operation ,which after got the file's URI
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            pluginScreenViewModel.updateFileURI(uri)
            pluginScreenViewModel.installPlugin()
        }
    }

    val lazyColumnState = rememberLazyListState()
    val totalListLength = shellScreenViewModel.shellOutputList.collectAsState().value.size

    LaunchedEffect(fileIntentUri) {
        val uri = fileIntentUri ?: return@LaunchedEffect
        navController.navigate("PluginScreen") {
            launchSingleTop = true
        }
        pluginScreenViewModel.updateFileURI(uri)
        pluginScreenViewModel.installPlugin()
        onHandlerEnded()
    }

    // Local Data
    var alertDialogStatus by rememberSaveable{ mutableStateOf(false) }
    var sourceDomainContent by rememberSaveable{ mutableStateOf("") }
    var sharedEvent by rememberSaveable { mutableStateOf<RootlessStoreError?>(null) }
    val context = LocalContext.current

    val scrollBehavior = when(currentDestination){
        "PluginScreen" -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        else -> TopAppBarDefaults.enterAlwaysScrollBehavior()
    }

    LaunchedEffect(0) {
        sourceScreenViewModel.sourceEvent.collect { event ->
            sharedEvent = event
        }
    }
    LaunchedEffect(1) {
        pluginScreenViewModel.pluginEvent.collect{ event ->
            sharedEvent = event
        }
    }
    LaunchedEffect(2) {
        marketScreenViewModel.marketEvent.collect{ event ->
            sharedEvent = event
        }
    }


    Scaffold(
        topBar = {
            when(currentDestination){
                "PluginScreen" -> PluginScreenNecessaryComponents.PluginScreenScreenTopAppBar(
                    pluginInfoCount = pluginInfoCount,
                    textButtonOnClick = {
                        pluginScreenViewModel.changeBadgeShowStatus()
                    },
                    scrollBehavior = scrollBehavior
                )
                "SourcesScreen" -> SourcesScreenNecessaryComponents.SourcesScreenTopAppBar(
                    iconButtonOnClick = {
                        alertDialogStatus = !alertDialogStatus
                    },
                    textButtonOnClick = {
                        sourceScreenViewModel.changeDeleterShowStatus()
                    },
                    sourceCount = sourceCount
                )
                "ExecuteScreen" -> executeScreenNecessaryComponents.ExecuteScreenTopAppBar(
                    scrollBehavior = scrollBehavior
                )
                "MarketScreen" -> MarketScreenNecessaryComponents.MarketScreenScreenTopAppBar(
                    sourceName = currentPluginSource!!.sourceName
                )
                "ShellScreen" -> ShellScreenNecessaryComponents.ShellScreenScreenTopAppBar(
                    onTopIconClick = {
                        lazyColumnState.scrollToItem(0)
                    },
                    onBottomIconClick = {

                        lazyColumnState.scrollToItem(totalListLength)
                    },
                    onDeleteIconClick = {
                        shellScreenViewModel.cleanShellOutputList()
                    }
                )
                else -> StartScreenNecessaryComponents.StartScreenTopAppBar(scrollBehavior)
            }
        },
        bottomBar = { StartScreenNecessaryComponents.StartScreenNavigationBar(navController)},
        floatingActionButton = {
            when(currentDestination){
                "PluginScreen" -> {
                    PluginScreenNecessaryComponents.PluginScreenFloatingButton{
                        openDocumentLauncher.launch(
                            arrayOf(
                                "application/zip",
                            )
                        )
                    }
                }
                "HomeScreen" -> {
                    StartScreenNecessaryComponents.StartScreenFloatingButton {
                        navController.navigate("ShellScreen")
                    }
                }
                else -> {}
            }
        },
        modifier = Modifier
            .nestedScroll(
                connection = scrollBehavior.nestedScrollConnection
            )
    ) { contentPadding->

        // Source Adding Dialog
        if (alertDialogStatus){
            StartScreenRepositoryDialog(
                sourceDomainContent,
                onDismissRequest =  {
                    alertDialogStatus = !alertDialogStatus
                },
                onConfirmButtonClick = {
                    sourceScreenViewModel.addOneSource(sourceURI = sourceDomainContent)
                    alertDialogStatus = !alertDialogStatus
                },
                onDismissButtonClick = {
                    alertDialogStatus = !alertDialogStatus
                },
                onTextFieldValueChange = { newValue -> sourceDomainContent = newValue }
            )
        }

        // Application Error Dialog
        if (sharedEvent is RootlessStoreError){
            StartScreenErrorDialog(sourceScreenViewModel, sharedEvent)
        }
        NavHost(
            navController = navController,
            startDestination = "HomeScreen"
        ){
            composable(
                route = "HomeScreen"
            ){
                HomeScreen(
                    contentPadding = contentPadding,
                    onChipClick = {
                        context.startActivity(Intent(context, ShizukuActivity::class.java))
                    }
                )
            }
            composable(
                route = "PluginScreen"
            ){
                RootlessStorePluginScreenContainer(
                    contentPadding = contentPadding,
                    navController = navController,
                    pluginScreenViewModel = pluginScreenViewModel,
                    executeScreenViewModel = executeScreenViewModel
                )
            }
            composable(
                route = "SourcesScreen"
            ){
                SourceScreen(
                    contentPadding = contentPadding,
                    sourceScreenViewModel = sourceScreenViewModel
                ){ pluginSourceLocal ->
                    marketScreenViewModel.updatePluginSourceUri(pluginSourceLocal.sourceRemoteEndpoint)
                    marketScreenViewModel.updateCurrentPluginSource(pluginSourceLocal)
                    navController.navigate("MarketScreen")
                }
            }
            composable(
                route = "MarketScreen"
            ){
                MarketScreen(
                    contentPadding = contentPadding,
                    marketScreenViewModel = marketScreenViewModel,
                    navController = navController
                )
            }
            composable(
                route = "ShellScreen"
            ){
                ShellScreen(
                    contentPaddingValues = contentPadding,
                    shellScreenViewModel = shellScreenViewModel,
                    lazyColumnState = lazyColumnState
                )
            }
            composable(
                route = "ExecuteScreen"
            ){
                ExecuteScreen(
                    contentPaddingValues = contentPadding,
                    executeScreenViewModel = executeScreenViewModel
                )
            }
        }
    }
}



//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@PreviewLightDark
//@Composable
//private fun _RootlessStoreStratScreenContainerPrevierer_(){
//    RootlessStoreTheme() {
//        RootlessStoreStartScreenContainer()
//    }
//}
