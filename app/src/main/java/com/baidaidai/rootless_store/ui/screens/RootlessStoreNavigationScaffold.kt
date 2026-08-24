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
import com.baidaidai.rootless_store.ui.components.executeScreen.ExecuteScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.marketScreen.MarketScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.pluginsScreen.PluginScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.settingScreen.SettingScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.shellScreen.ShellScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.sourcesScreen.SourceScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.startScreen.StartScreenErrorDialog
import com.baidaidai.rootless_store.ui.components.startScreen.StartScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.components.startScreen.StartScreenRepositoryDialog
import com.baidaidai.rootless_store.ui.components.thirdPartyNotificationScreen.ThirdPartyNotificationScreenNecessaryComponents
import com.baidaidai.rootless_store.ui.model.RootlessStoreExecuteScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreMarketScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStorePluginScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreShellScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreThirdPartyNotificationScreenViewModel
import com.baidaidai.rootless_store.ui.model.RootlessStoreCodeBrickViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootlessStoreNavigationScaffold(
    pluginScreenViewModel: RootlessStorePluginScreenViewModel = hiltViewModel(),
    sourceScreenViewModel: RootlessStoreSourceScreenViewModel = hiltViewModel(),
    incomingPackageUri: Uri?,
    onIncomingPackageConsumed: () -> Unit
){
    // VM & VM Data
    val marketScreenViewModel = hiltViewModel<RootlessStoreMarketScreenViewModel>()
    val shellScreenViewModel = hiltViewModel<RootlessStoreShellScreenViewModel>()
    val thirdPartyNotificationScreenViewModel = hiltViewModel<RootlessStoreThirdPartyNotificationScreenViewModel>()
    val codeBrickViewModel = hiltViewModel<RootlessStoreCodeBrickViewModel>()
    val pluginCount by pluginScreenViewModel.pluginCount.collectAsState()
    val pluginSourceCount by sourceScreenViewModel.pluginSourceCount.collectAsState()

    // Navigation
    val navigationBackStack = rememberNavBackStack(HomeScreenKey)
    val currentDestination = navigationBackStack.lastOrNull()

    val currentPluginSource by marketScreenViewModel.currentPluginSource.collectAsState()


    // Define the operation ,which after got the file's URI
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            pluginScreenViewModel.setPendingLocalPackageUri(uri)
            pluginScreenViewModel.installLocalPackage()
        }
    }

    val lazyColumnState = rememberLazyListState() /*TODO("Can migration Intro VM")*/
    val shellOutputCount = shellScreenViewModel.shellOutputs.collectAsState().value.size

    // Local Data
    var isSourceDialogVisible by rememberSaveable{ mutableStateOf(false) }
    var sourceRemoteEndpoint by rememberSaveable{ mutableStateOf("") }
    var currentError by rememberSaveable { mutableStateOf<RootlessStoreError?>(null) }
    val context = LocalContext.current
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    val scrollBehavior = when(currentDestination){
        PluginScreenKey, CodeBrickScreenKey, MarketScreenKey, SettingScreenKey, ThirdPartyNotificationScreenKey -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        else -> TopAppBarDefaults.pinnedScrollBehavior()
    }

    // Reactive Style
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isExpandedWidth = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    val isExpandedHeight = windowSizeClass.isHeightAtLeastBreakpoint(
        WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND
    )
    val isMediumWidth = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )
    val isMediumHeight = windowSizeClass.isHeightAtLeastBreakpoint(
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
            currentError = event
        }
    }
    LaunchedEffect(1) {
        pluginScreenViewModel.pluginEvent.collect{ event ->
            currentError = event
        }
    }
    LaunchedEffect(2) {
        marketScreenViewModel.marketEvent.collect{ event ->
            currentError = event
        }
    }
    LaunchedEffect(3) {
        codeBrickViewModel.codeBrickEvent.collect { event ->
            currentError = event
        }
    }
    LaunchedEffect(incomingPackageUri) {
        val uri = incomingPackageUri ?: return@LaunchedEffect

        navigationBackStack.add(PluginScreenKey)
        pluginScreenViewModel.setPendingLocalPackageUri(uri)
        pluginScreenViewModel.installLocalPackage()
        onIncomingPackageConsumed()
    }


    @Composable
    fun resolveExecuteScreenViewModel(pluginId: String): RootlessStoreExecuteScreenViewModel {
        val viewModel = hiltViewModel<RootlessStoreExecuteScreenViewModel>(viewModelStoreOwner = viewModelStoreOwner, key = pluginId)
        return viewModel
    }

    val currentExecuteViewModel =
        if (currentDestination is ExecuteScreenKey) {
            resolveExecuteScreenViewModel(currentDestination.pluginId)
        }else{
            resolveExecuteScreenViewModel("abc")
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
                    PluginScreenKey -> PluginScreenNecessaryComponents.PluginScreenTopAppBar(
                        pluginCount = pluginCount,
                        onEditClick = {
                            pluginScreenViewModel.toggleBadgeVisibility()
                        },
                        scrollBehavior = scrollBehavior
                    )
                    CodeBrickScreenKey -> CodeBrickScreenNecessaryComponents.CodeBrickScreenTopAppBar(
                        scrollBehavior = scrollBehavior
                    )
                    SourceScreenKey -> SourceScreenNecessaryComponents.SourceScreenTopAppBar(
                        onAddClick = {
                            isSourceDialogVisible = !isSourceDialogVisible
                        },
                        onEditClick = {
                            sourceScreenViewModel.toggleDeleteActionVisibility()
                        },
                        pluginSourceCount = pluginSourceCount
                    )
                    is ExecuteScreenKey -> {
                        ExecuteScreenNecessaryComponents.ExecuteScreenTopAppBar(
                            scrollBehavior = scrollBehavior,
                            onAbortClick = {
                                currentExecuteViewModel.abortPluginProcess(currentDestination.pluginId)
                            },
                            onBackClick = {
                                navigationBackStack.removeLastOrNull()
                            },
                            onShareClick = {
                                val executionLog = currentExecuteViewModel?.exportExecutionLog()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, executionLog)

                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share"))
                            }
                        )
                    }
                    MarketScreenKey -> MarketScreenNecessaryComponents.MarketScreenTopAppBar(
                        sourceName = currentPluginSource!!.sourceName,
                        scrollBehavior = scrollBehavior
                    )
                    ShellScreenKey -> ShellScreenNecessaryComponents.ShellScreenTopAppBar(
                        onScrollToTop = {
                            lazyColumnState.scrollToItem(0)
                        },
                        onScrollToBottom = {

                            lazyColumnState.scrollToItem(shellOutputCount)
                        },
                        onClearOutput = {
                            shellScreenViewModel.resetShellOutput()
                        }
                    )
                    SettingScreenKey -> SettingScreenNecessaryComponents.SettingScreenTopAppBar(
                        scrollBehavior = scrollBehavior
                    )
                    ThirdPartyNotificationScreenKey -> ThirdPartyNotificationScreenNecessaryComponents.ThirdPartyNotificationScreenTopAppBar(
                        scrollBehavior = scrollBehavior,
                        onSaveClick = {
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
                            isMenuExpanded = codeBrickScreenUiState.isCreationMenuExpanded,
                            onCreateClick = {
                                codeBrickViewModel.setCodeBrickEditorVisible(true)
                                codeBrickViewModel.setCreationMenuExpanded()
                            },
                            onImportJsonClick = {
                                codeBrickViewModel.addCodeBrickFromClipboard()
                                codeBrickViewModel.setCreationMenuExpanded()
                            },
                            onExpandedChange = {
                                codeBrickViewModel.setCreationMenuExpanded(it)
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
            if (isSourceDialogVisible){
                StartScreenRepositoryDialog(
                    sourceRemoteEndpoint,
                    onDismissRequest =  {
                        isSourceDialogVisible = !isSourceDialogVisible
                    },
                    onAddPluginSource = {
                        sourceScreenViewModel.addPluginSource(sourceRemoteEndpoint = sourceRemoteEndpoint)
                        isSourceDialogVisible = !isSourceDialogVisible
                    },
                    onCancel = {
                        isSourceDialogVisible = !isSourceDialogVisible
                    },
                    onSourceRemoteEndpointChange = { newValue -> sourceRemoteEndpoint = newValue }
                )
            }

            // Application Error Dialog
            if (currentError is RootlessStoreError){
                StartScreenErrorDialog(sourceScreenViewModel, currentError)
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
                        PluginScreen(
                            contentPadding = contentPadding,
                            pluginScreenViewModel = pluginScreenViewModel,
                            onNavigateToExecuteScreen = { pluginId, shouldExecutePlugin ->
                                navigationBackStack
                                    .add(ExecuteScreenKey(pluginId,shouldExecutePlugin))
                            },
                            onAbortPlugin = { pluginId ->
                                currentExecuteViewModel.abortPluginProcess(pluginId)
                            },
                            onActivateOneTimePlugin = { pluginId ->
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
                                codeBrickViewModel.setCreationMenuExpanded()
                            }
                        )
                    }
                    entry<SourceScreenKey> {
                        SourceScreen(
                            contentPadding = contentPadding,
                            sourceScreenViewModel = sourceScreenViewModel
                        ){ pluginSource ->
                            marketScreenViewModel.setPluginSourceEndpoint(pluginSource.sourceRemoteEndpoint)
                            marketScreenViewModel.selectPluginSource(pluginSource)
                            navigationBackStack.add(MarketScreenKey)
                        }
                    }
                    entry<MarketScreenKey> {
                        MarketScreen(
                            contentPadding = contentPadding,
                            marketScreenViewModel = marketScreenViewModel,
                            onNavigateToPluginScreen = {
                                navigationBackStack.add(PluginScreenKey)
                            }
                        )
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
                        val executeScreenViewModel = hiltViewModel<RootlessStoreExecuteScreenViewModel>(key = executeScreenKey.pluginId, viewModelStoreOwner = viewModelStoreOwner)

                        val pluginId = executeScreenKey.pluginId
                        val shouldExecutePlugin = executeScreenKey.shouldExecutePlugin

                        Log.d("ExecuteScreenKey.pluginId",pluginId)
                        Log.d("ExecuteScreenKey.shouldExecutePlugin",shouldExecutePlugin.toString())

                        // Function debouncing
                        LaunchedEffect(pluginId, shouldExecutePlugin) {
                            if (shouldExecutePlugin) {
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
