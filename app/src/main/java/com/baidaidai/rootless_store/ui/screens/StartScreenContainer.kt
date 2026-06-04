package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.baidaidai.rootless_store.ShizukuActivity
import com.baidaidai.rootless_store.components.executeScreen.executeScreenNecessaryComponents
import com.baidaidai.rootless_store.components.marketScreen.MarketScreenNecessaryComponents
import com.baidaidai.rootless_store.components.pluginsScreen.PluginScreenNecessaryComponents
import com.baidaidai.rootless_store.components.settingScreen.SettingScreenNecessaryComponents
import com.baidaidai.rootless_store.components.shellScreen.ShellScreenNecessaryComponents
import com.baidaidai.rootless_store.components.sourcesScreen.SourcesScreenNecessaryComponents
import com.baidaidai.rootless_store.components.startScreen.StartScreenErrorDialog
import com.baidaidai.rootless_store.components.startScreen.StartScreenRepositoryDialog
import com.baidaidai.rootless_store.components.startScreen.StartScreenNecessaryComponents
import com.baidaidai.rootless_store.components.thirdPartyNotificationScreen.ThirdPartyNotificationScreenNecessaryComponents
import com.baidaidai.rootless_store.domain.error.RootlessStoreError
import com.baidaidai.rootless_store.domain.navigation.`interface`.RootlessNavigationKey
import com.baidaidai.rootless_store.domain.navigation.model.ExecuteScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.HomeScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.MarketScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.PluginScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.SettingScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ShellScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.SourceScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ThirdPartyNotificationScreenKey
import com.baidaidai.rootless_store.ui.model.RootLessStoreExecuteScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreMarketScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStorePluginScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreShellScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreThirdPartyNotificationScreenViewModel

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
    val shellScreenViewModel = hiltViewModel<RootLessStoreShellScreenViewModel>()
    val thirdPartyNotificationScreenViewModel = hiltViewModel<RootLessStoreThirdPartyNotificationScreenViewModel>()
    val pluginInfoCount by pluginScreenViewModel.pluginInfoCount.collectAsState()
    val sourceCount by sourceScreenViewModel.sourceCount.collectAsState()

    // Navigation
    val navigationBackStack = rememberNavBackStack(HomeScreenKey)
    val currentDestination = navigationBackStack.lastOrNull()

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

    val lazyColumnState = rememberLazyListState() /*TODO("Can migration Intro VM")*/
    val totalListLength = shellScreenViewModel.shellOutputList.collectAsState().value.size

    // Local Data
    var alertDialogStatus by rememberSaveable{ mutableStateOf(false) }
    var sourceDomainContent by rememberSaveable{ mutableStateOf("") }
    var sharedEvent by rememberSaveable { mutableStateOf<RootlessStoreError?>(null) }
    val context = LocalContext.current
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    val scrollBehavior = when(currentDestination){
        PluginScreenKey, MarketScreenKey, SettingScreenKey, ThirdPartyNotificationScreenKey -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        else -> TopAppBarDefaults.enterAlwaysScrollBehavior()
    }

    // Effects
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
    LaunchedEffect(fileIntentUri) {
        val uri = fileIntentUri ?: return@LaunchedEffect
        /* TODO("navController") */
        navigationBackStack.add(PluginScreenKey)
        pluginScreenViewModel.updateFileURI(uri)
        pluginScreenViewModel.installPlugin()
        onHandlerEnded()
    }


    @Composable
    fun executeViewModelBuilder(pluginID: String): RootLessStoreExecuteScreenViewModel {
        val viewModel = hiltViewModel<RootLessStoreExecuteScreenViewModel>(viewModelStoreOwner = viewModelStoreOwner, key = pluginID)
        return viewModel
    }

    val currentExecuteViewModel =
        if (currentDestination is ExecuteScreenKey) {
            executeViewModelBuilder(currentDestination.pluginID)
        }else{
            executeViewModelBuilder("abc")
        }

    Scaffold(
        topBar = {
            when(currentDestination){
                PluginScreenKey -> PluginScreenNecessaryComponents.PluginScreenScreenTopAppBar(
                    pluginInfoCount = pluginInfoCount,
                    textButtonOnClick = {
                        pluginScreenViewModel.changeBadgeShowStatus()
                    },
                    scrollBehavior = scrollBehavior
                )
                SourceScreenKey -> SourcesScreenNecessaryComponents.SourcesScreenTopAppBar(
                    iconButtonOnClick = {
                        alertDialogStatus = !alertDialogStatus
                    },
                    textButtonOnClick = {
                        sourceScreenViewModel.changeDeleterShowStatus()
                    },
                    sourceCount = sourceCount
                )
                is ExecuteScreenKey -> {
                    executeScreenNecessaryComponents.ExecuteScreenTopAppBar(
                        scrollBehavior = scrollBehavior,
                        onExecuteScreenStopButtonClick = {
                            currentExecuteViewModel.abortPluginProcess(currentDestination.pluginID)
                        },
                        onExecuteScreenBackButtonClick = {
                            navigationBackStack.removeLastOrNull()
                        },
                        onExecuteScreenShareButtonClick = {
                            val executeLog = currentExecuteViewModel?.exportExecuteLog()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, executeLog)

                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share"))
                        }
                    )
                }
                MarketScreenKey -> MarketScreenNecessaryComponents.MarketScreenScreenTopAppBar(
                    sourceName = currentPluginSource!!.sourceName,
                    scrollBehavior = scrollBehavior
                )
                ShellScreenKey -> ShellScreenNecessaryComponents.ShellScreenScreenTopAppBar(
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
                SettingScreenKey -> SettingScreenNecessaryComponents.SettingScreenTopAppBar(
                    scrollBehavior = scrollBehavior
                )
                ThirdPartyNotificationScreenKey -> ThirdPartyNotificationScreenNecessaryComponents.ThirdPartyNotificationScreenTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onSaveButtonClick = {
                        thirdPartyNotificationScreenViewModel.onSubmitClick()
                    }
                )
                else -> StartScreenNecessaryComponents.StartScreenTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onSettingClick = {
                        navigationBackStack.add(SettingScreenKey)
                    }
                )
            }
        },
        bottomBar = {
            StartScreenNecessaryComponents
                .StartScreenNavigationBar(
                    currentDestination = navigationBackStack.last() as RootlessNavigationKey,
                ){ rootlessNavigationKey ->
                    navigationBackStack.add(rootlessNavigationKey)
                }
        },
        floatingActionButton = {
            when(currentDestination){
                PluginScreenKey -> {
                    PluginScreenNecessaryComponents.PluginScreenFloatingButton{
                        openDocumentLauncher.launch(
                            arrayOf(
                                "application/zip",
                            )
                        )
                    }
                }
                HomeScreenKey -> {
                    StartScreenNecessaryComponents.StartScreenFloatingButton {
                        navigationBackStack.add(ShellScreenKey)
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
                    sourceScreenViewModel.addOneSourceByDefault(sourceURI = sourceDomainContent)
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

        NavDisplay(
            backStack = navigationBackStack,
            entryProvider = entryProvider {
                entry<HomeScreenKey>{
                    HomeScreen(
                        contentPadding = contentPadding,
                        onChipClick = {
                            context.startActivity(Intent(context, ShizukuActivity::class.java))
                        }
                    )
                }
                entry<PluginScreenKey> {
                    RootlessStorePluginScreenContainer(
                        contentPadding = contentPadding,
                        pluginScreenViewModel = pluginScreenViewModel,
                        navigateToExecuteScreen = { pluginID, isExecutePlugin ->
                            navigationBackStack
                                .add(ExecuteScreenKey(pluginID,isExecutePlugin))
                        },
                        onAbortOnePlugin = { pluginID ->
                            currentExecuteViewModel.abortPluginProcess(pluginID)
                        }
                    )
                }
                entry<SourceScreenKey> {
                    SourceScreen(
                        contentPadding = contentPadding,
                        sourceScreenViewModel = sourceScreenViewModel
                    ){ pluginSourceLocal ->
                        marketScreenViewModel.updatePluginSourceUri(pluginSourceLocal.sourceRemoteEndpoint)
                        marketScreenViewModel.updateCurrentPluginSource(pluginSourceLocal)
                        navigationBackStack.add(MarketScreenKey)
                    }
                }
                entry<MarketScreenKey> {
                    MarketScreen(
                        contentPadding = contentPadding,
                        marketScreenViewModel = marketScreenViewModel
                    ){
                        navigationBackStack.add(PluginScreenKey)
                    }
                }
                entry<ShellScreenKey> {
                    ShellScreen(
                        contentPaddingValues = contentPadding,
                        shellScreenViewModel = shellScreenViewModel,
                        lazyColumnState = lazyColumnState
                    )
                }
                entry<ExecuteScreenKey> { executeScreenKey ->

                    // The overall constructor of ExecuteScreenViewModel
                    val executeScreenViewModel = hiltViewModel<RootLessStoreExecuteScreenViewModel>(key = executeScreenKey.pluginID, viewModelStoreOwner = viewModelStoreOwner)

                    val pluginID = executeScreenKey.pluginID
                    val isExecutePlugin = executeScreenKey.isExecutePlugin

                    Log.d("ExecuteScreenKey.pluginID",pluginID)
                    Log.d("ExecuteScreenKey.isExecutePlugin",isExecutePlugin.toString())

                    // Function debouncing
                    LaunchedEffect(pluginID, isExecutePlugin) {
                        if (isExecutePlugin) {
                            executeScreenViewModel.executeOnePlugin(pluginID)
                        }
                    }

                    ExecuteScreen(
                        contentPaddingValues = contentPadding,
                        executeScreenViewModel = executeScreenViewModel
                    )
                }
                entry<SettingScreenKey> {
                    SettingScreen(
                        contentPaddingValues = contentPadding,
                        onThirdPartyNotificationSettingClick = {
                            navigationBackStack.add(ThirdPartyNotificationScreenKey)
                        }
                    )
                }
                entry<ThirdPartyNotificationScreenKey> {
                    ThirdPartyNotificationScreen(
                        contentPaddingValues = contentPadding,
                        thirdPartyNotificationScreenViewModel = thirdPartyNotificationScreenViewModel
                    )
                }
            }
        )

    }
}