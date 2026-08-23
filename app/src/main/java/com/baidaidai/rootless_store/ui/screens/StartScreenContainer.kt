package com.baidaidai.rootless_store.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
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
import androidx.window.core.layout.WindowSizeClass
import com.baidaidai.rootless_store.ShizukuActivity
import com.baidaidai.rootless_store.domain.error.RootlessStoreError
import com.baidaidai.rootless_store.domain.navigation.`interface`.RootlessNavigationKey
import com.baidaidai.rootless_store.domain.navigation.model.CodeBrickScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ExecuteScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.HomeScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.MarketScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.PluginScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.SettingScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ShellScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.SourceScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ThirdPartyNotificationScreenKey
import com.baidaidai.rootless_store.ui.adaptive.RootlessStoreWindowSize
import com.baidaidai.rootless_store.ui.components.codeBrickScreen.CodeBrickScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.executeScreen.executeScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.marketScreen.MarketScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.pluginsScreen.PluginScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.settingScreen.SettingScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.shellScreen.ShellScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.sourcesScreen.SourcesScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.startScreen.StartScreenErrorDialog
import com.baidaidai.rootless_store.ui.components.startScreen.StartScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.startScreen.StartScreenRepositoryDialog
import com.baidaidai.rootless_store.ui.components.thirdPartyNotificationScreen.ThirdPartyNotificationScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.model.RootLessStoreExecuteScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreMarketScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStorePluginScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreShellScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreThirdPartyNotificationScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreCodeBrickViewModel

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
    val codeBrickViewModel = hiltViewModel<RootlessStoreCodeBrickViewModel>()
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
            pluginScreenViewModel.updateFileUri(uri)
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
        PluginScreenKey, CodeBrickScreenKey, MarketScreenKey, SettingScreenKey, ThirdPartyNotificationScreenKey -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        else -> TopAppBarDefaults.pinnedScrollBehavior()
    }

    // Reactive Style
    val _windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isExpandedWidth = _windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    val isExpandedHeight = _windowSizeClass.isHeightAtLeastBreakpoint(
        WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND
    )
    val isMediumWidth = _windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )
    val isMediumHeight = _windowSizeClass.isHeightAtLeastBreakpoint(
        WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
    )
    val rootlessStoreWidthWindowSize = when {
        isExpandedWidth -> RootlessStoreWindowSize.Expanded
        isMediumWidth -> RootlessStoreWindowSize.Medium
        else -> RootlessStoreWindowSize.Compact
    }
    val rootlessStoreHeightWindowSize = when {
        isExpandedHeight -> RootlessStoreWindowSize.Expanded
        isMediumHeight -> RootlessStoreWindowSize.Medium
        else -> RootlessStoreWindowSize.Compact
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
    LaunchedEffect(3) {
        codeBrickViewModel.codeBrickEvent.collect { event ->
            sharedEvent = event
        }
    }
    LaunchedEffect(fileIntentUri) {
        val uri = fileIntentUri ?: return@LaunchedEffect

        navigationBackStack.add(PluginScreenKey)
        pluginScreenViewModel.updateFileUri(uri)
        pluginScreenViewModel.installPlugin()
        onHandlerEnded()
    }


    @Composable
    fun executeViewModelBuilder(pluginId: String): RootLessStoreExecuteScreenViewModel {
        val viewModel = hiltViewModel<RootLessStoreExecuteScreenViewModel>(viewModelStoreOwner = viewModelStoreOwner, key = pluginId)
        return viewModel
    }

    val currentExecuteViewModel =
        if (currentDestination is ExecuteScreenKey) {
            executeViewModelBuilder(currentDestination.pluginId)
        }else{
            executeViewModelBuilder("abc")
        }

    Row(modifier = Modifier.fillMaxSize()) {
        if(rootlessStoreWidthWindowSize == RootlessStoreWindowSize.Expanded){
            StartScreenNecessaryComponents.StartScreenExpressiveNavigationRail(
                currentDestination = navigationBackStack.last() as RootlessNavigationKey,
            ) { rootlessNavigationKey ->
                navigationBackStack.add(rootlessNavigationKey)
            }
        }else if(rootlessStoreWidthWindowSize == RootlessStoreWindowSize.Medium){
            StartScreenNecessaryComponents.StartScreenNavigationRail(
                currentDestination = navigationBackStack.last() as RootlessNavigationKey,
            ) { rootlessNavigationKey ->
                navigationBackStack.add(rootlessNavigationKey)
            }
        }

        Scaffold(
            topBar = {
                when(currentDestination){
                    PluginScreenKey -> PluginScreenNecessaryComponents.PluginScreenScreenTopAppBar(
                        pluginInfoCount = pluginInfoCount,
                        textButtonOnClick = {
                            pluginScreenViewModel.toggleBadgeVisibility()
                        },
                        scrollBehavior = scrollBehavior
                    )
                    CodeBrickScreenKey -> CodeBrickScreenNecessaryComponents.CodeBrickScreenTopAppBar(
                        scrollBehavior = scrollBehavior
                    )
                    SourceScreenKey -> SourcesScreenNecessaryComponents.SourcesScreenTopAppBar(
                        iconButtonOnClick = {
                            alertDialogStatus = !alertDialogStatus
                        },
                        textButtonOnClick = {
                            sourceScreenViewModel.toggleDeleteActionVisibility()
                        },
                        sourceCount = sourceCount
                    )
                    is ExecuteScreenKey -> {
                        executeScreenNecessaryComponents.ExecuteScreenTopAppBar(
                            scrollBehavior = scrollBehavior,
                            onExecuteScreenStopButtonClick = {
                                currentExecuteViewModel.abortPluginProcess(currentDestination.pluginId)
                            },
                            onExecuteScreenBackButtonClick = {
                                navigationBackStack.removeLastOrNull()
                            },
                            onExecuteScreenShareButtonClick = {
                                val executionLog = currentExecuteViewModel?.exportExecutionLog()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, executionLog)

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
                if (rootlessStoreWidthWindowSize == RootlessStoreWindowSize.Compact){
                    StartScreenNecessaryComponents.StartScreenNavigationBar(
                        currentDestination = navigationBackStack.last() as RootlessNavigationKey
                    ){ rootlessNavigationKey ->
                        navigationBackStack.add(rootlessNavigationKey)
                    }
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

                    CodeBrickScreenKey -> {
                        val codeBrickScreenUiState by codeBrickViewModel.codeBrickScreenUiState.collectAsState()
                        CodeBrickScreenNecessaryComponents.CodeBrickScreenFloatingButton(
                            isButtonMenuExpanded = codeBrickScreenUiState.isButtonMenuExpanded,
                            onHandMenuItemClick = {
                                codeBrickViewModel.setBrickEditorVisible(true)
                                codeBrickViewModel.setButtonMenuExpanded()
                            },
                            onJsonMenuItemClick = {
                                codeBrickViewModel.createCodeBrickByJson()
                                codeBrickViewModel.setButtonMenuExpanded()
                            },
                            onButtonMenuClick = {
                                codeBrickViewModel.setButtonMenuExpanded(it)
                            }
                        )
                    }

                    else -> {}

                }
            },
            modifier = Modifier
                .nestedScroll(
                    connection = scrollBehavior.nestedScrollConnection
                )
                .weight(1f)
        ) { contentPadding->

            // Source Adding Dialog
            if (alertDialogStatus){
                StartScreenRepositoryDialog(
                    sourceDomainContent,
                    onDismissRequest =  {
                        alertDialogStatus = !alertDialogStatus
                    },
                    onConfirmButtonClick = {
                        sourceScreenViewModel.addSourceByDefault(sourceUri = sourceDomainContent)
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
                            rootlessStoreHeightWindowSize = rootlessStoreHeightWindowSize,
                            rootlessStoreWidthWindowSize = rootlessStoreWidthWindowSize,
                            onChipClick = {
                                context.startActivity(Intent(context, ShizukuActivity::class.java))
                            }
                        )
                    }
                    entry<PluginScreenKey> {
                        RootlessStorePluginScreenContainer(
                            contentPadding = contentPadding,
                            pluginScreenViewModel = pluginScreenViewModel,
                            navigateToExecuteScreen = { pluginId, isExecutePlugin ->
                                navigationBackStack
                                    .add(ExecuteScreenKey(pluginId,isExecutePlugin))
                            },
                            onAbortPlugin = { pluginId ->
                                currentExecuteViewModel.abortPluginProcess(pluginId)
                            },
                            onActiveOneTimePlugin = { pluginId ->
                                currentExecuteViewModel.executePlugin(pluginId)
                            }
                        )
                    }
                    entry<CodeBrickScreenKey>{
                        CodeBrickScreen(
                            contentPaddingValues = contentPadding,
                            codeBrickViewModel = codeBrickViewModel,
                            rootlessStoreWindowSize = rootlessStoreWidthWindowSize,
                            onBackgroundClick = {
                                codeBrickViewModel.setButtonMenuExpanded()
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
                        val executeScreenViewModel = hiltViewModel<RootLessStoreExecuteScreenViewModel>(key = executeScreenKey.pluginId, viewModelStoreOwner = viewModelStoreOwner)

                        val pluginId = executeScreenKey.pluginId
                        val isExecutePlugin = executeScreenKey.isExecutePlugin

                        Log.d("ExecuteScreenKey.pluginId",pluginId)
                        Log.d("ExecuteScreenKey.isExecutePlugin",isExecutePlugin.toString())

                        // Function debouncing
                        LaunchedEffect(pluginId, isExecutePlugin) {
                            if (isExecutePlugin) {
                                executeScreenViewModel.executePlugin(pluginId)
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
}
