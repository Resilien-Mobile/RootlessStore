package com.baidaidai.rootless_store.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.environment.ResolveEnvironmentShareUriUseCase
import com.baidaidai.rootless_store.application.install.InstallLocalPackageUseCase
import com.baidaidai.rootless_store.application.install.ResolveLocalPackageMetaInfoUseCase
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus
import com.baidaidai.rootless_store.application.plugin.ObservePluginsUseCase
import com.baidaidai.rootless_store.application.plugin.AbortPluginProcessUseCase
import com.baidaidai.rootless_store.application.plugin.UninstallPluginUseCase
import com.baidaidai.rootless_store.application.plugin.ObservePluginStatusUseCase
import com.baidaidai.rootless_store.application.plugin.ObservePluginTotalCountUseCase
import com.baidaidai.rootless_store.application.environment.ObserveEnvironmentsUseCase
import com.baidaidai.rootless_store.application.environment.ObserveEnvironmentStatusUseCase
import com.baidaidai.rootless_store.application.environment.DisableEnvironmentUseCase
import com.baidaidai.rootless_store.application.environment.EnableEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.DisablePluginUseCase
import com.baidaidai.rootless_store.application.plugin.EnablePluginUseCase
import com.baidaidai.rootless_store.application.environment.UninstallEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.ResolvePluginShareUriUseCase
import com.baidaidai.rootless_store.application.plugin.ResolvePluginWebUiUriUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginStatus
import com.baidaidai.rootless_store.ui.uistate.RootlessStorePluginScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.github.michaelbull.result.getError
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class RootlessStorePluginScreenViewModel @Inject constructor(
    private val observePluginsUseCase: ObservePluginsUseCase,
    private val observeEnvironmentsUseCase: ObserveEnvironmentsUseCase,
    private val observeEnvironmentStatusUseCase: ObserveEnvironmentStatusUseCase,
    private val observePluginStatusUseCase: ObservePluginStatusUseCase,
    private val installLocalPackageUseCase: InstallLocalPackageUseCase,
    private val enablePluginUseCase: EnablePluginUseCase,
    private val disablePluginUseCase: DisablePluginUseCase,
    private val enableEnvironmentUseCase: EnableEnvironmentUseCase,
    private val disableEnvironmentUseCase: DisableEnvironmentUseCase,
    private val uninstallPluginUseCase: UninstallPluginUseCase,
    private val uninstallEnvironmentUseCase: UninstallEnvironmentUseCase,
    private val abortPluginProcessUseCase: AbortPluginProcessUseCase,
    private val resolvePluginShareUriUseCase: ResolvePluginShareUriUseCase,
    private val resolvePluginWebUiUriUseCase: ResolvePluginWebUiUriUseCase,
    private val resolveEnvironmentShareUriUseCase: ResolveEnvironmentShareUriUseCase,
    private val resolveLocalPackageMetaInfoUseCase: ResolveLocalPackageMetaInfoUseCase,
    observePluginTotalCountUseCase: ObservePluginTotalCountUseCase
): ViewModel() {

    private val _isBadgeVisible = MutableStateFlow(false)
    private val _pluginScreenUiState = MutableStateFlow(RootlessStorePluginScreenUiState())

    private val _pluginError = MutableSharedFlow<PluginError?>()
    val pluginError = _pluginError.asSharedFlow()

    val plugins = observePluginsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<PluginManifest>()
    )

    val environments = observeEnvironmentsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<EnvironmentManifest>()
    )

    val pluginCount = observePluginTotalCountUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val isBadgeVisible = _isBadgeVisible.asStateFlow()
    val pluginScreenUiState = _pluginScreenUiState.asStateFlow()

    /**
     * 按 pluginId 单独观察某个插件的状态。
     * 卡片拿到 PluginManifest 后，通过本方法订阅自己的 PluginStatus，避免对齐两条 List 流。
     */
    fun observePluginStatus(pluginId: String): Flow<PluginStatus?> =
        observePluginStatusUseCase(pluginId)

    /**
     * 按 environmentId 单独观察某个环境的状态。
     */
    fun observeEnvironmentStatus(environmentId: String): Flow<EnvironmentStatus?> =
        observeEnvironmentStatusUseCase(environmentId)

    fun prepareLocalPackageInstall(
        packageUri: Uri
    ) {
        viewModelScope.launch {
            val localPackageMetaInfo = resolveLocalPackageMetaInfoUseCase(packageUri)

            if (localPackageMetaInfo == null) {
                installLocalPackage(packageUri)
                return@launch
            }

            _pluginScreenUiState.update { uiState ->
                uiState.copy(
                    localPackageUri = packageUri,
                    localPackageMetaInfo = localPackageMetaInfo,
                    isLocalPackageConfirmationSheetVisible = true
                )
            }
        }
    }

    fun confirmLocalPackageInstall() {
        val packageUri = pluginScreenUiState.value.localPackageUri ?: return
        clearLocalPackageInstallConfirmation()
        installLocalPackage(packageUri)
    }

    fun dismissLocalPackageInstallConfirmation() {
        clearLocalPackageInstallConfirmation()
    }

    private fun installLocalPackage(
        packageUri: Uri
    ){
        viewModelScope.launch {
            // 会返回 Error？，若 Error 则直接返回，若没有 Error 则推 Null
            val installationError = installLocalPackageUseCase(packageUri).getError()
            _pluginError.emit(installationError)
        }
    }

    private fun clearLocalPackageInstallConfirmation() {
        _pluginScreenUiState.update { uiState ->
            uiState.copy(
                localPackageUri = null,
                localPackageMetaInfo = null,
                isLocalPackageConfirmationSheetVisible = false
            )
        }
    }

    fun uninstallPlugin(
        pluginManifest: PluginManifest
    ){
        viewModelScope.launch {
            uninstallPluginUseCase(pluginManifest)
        }
    }

    fun uninstallEnvironment(
        environmentManifest: EnvironmentManifest
    ){
        viewModelScope.launch {
            uninstallEnvironmentUseCase(environmentManifest)
        }
    }

    fun setPluginEnabled(
        pluginId: String,
        isEnabled: Boolean
    ){
        viewModelScope.launch {
            if (isEnabled) {
                enablePluginUseCase(pluginId)
            } else {
                disablePluginUseCase(pluginId)
            }
        }
    }

    fun setEnvironmentEnabled(
        environmentId: String,
        isEnabled: Boolean
    ){
        viewModelScope.launch {
            if (isEnabled) {
                enableEnvironmentUseCase(environmentId)
            } else {
                disableEnvironmentUseCase(environmentId)
            }
        }
    }

    fun toggleBadgeVisibility(){
        _isBadgeVisible.update { !it }
    }

    fun resolvePluginShareUri(
        pluginManifest: PluginManifest
    ): Uri {
        val shareUri = resolvePluginShareUriUseCase(pluginManifest)
        return shareUri
    }

    fun resolvePluginWebUiUri(
        pluginManifest: PluginManifest
    ): String {
        val webUiUri = resolvePluginWebUiUriUseCase(pluginManifest)
        return webUiUri
    }

    fun resolveEnvironmentShareUri(
        environmentManifest: EnvironmentManifest
    ): Uri {
        val shareUri = resolveEnvironmentShareUriUseCase(environmentManifest)
        return shareUri
    }


}
