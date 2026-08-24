package com.baidaidai.rootless_store.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.environment.ResolveEnvironmentShareUriUseCase
import com.baidaidai.rootless_store.application.install.InstallLocalPackageUseCase
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.application.plugin.ObservePluginsUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.application.plugin.AbortPluginProcessUseCase
import com.baidaidai.rootless_store.application.plugin.UninstallPluginUseCase
import com.baidaidai.rootless_store.application.plugin.ObservePluginCountUseCase
import com.baidaidai.rootless_store.application.environment.ObserveEnvironmentsUseCase
import com.baidaidai.rootless_store.application.environment.DisableEnvironmentUseCase
import com.baidaidai.rootless_store.application.environment.EnableEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.DisablePluginUseCase
import com.baidaidai.rootless_store.application.plugin.EnablePluginUseCase
import com.baidaidai.rootless_store.application.environment.UninstallEnvironmentUseCase
import com.baidaidai.rootless_store.application.plugin.ResolvePluginShareUriUseCase
import com.baidaidai.rootless_store.application.plugin.ResolvePluginWebUiUriUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class RootlessStorePluginScreenViewModel @Inject constructor(
    private val observePluginsUseCase: ObservePluginsUseCase,
    private val observeEnvironmentsUseCase: ObserveEnvironmentsUseCase,
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
    observePluginCountUseCase: ObservePluginCountUseCase
): ViewModel() {

    private val _pendingLocalPackageUri = MutableStateFlow<Uri>(value = Uri.EMPTY)
    private val _isBadgeVisible = MutableStateFlow(false)

    private val _pluginError = MutableSharedFlow<PluginError?>()
    val pluginError = _pluginError.asSharedFlow()

    val plugins = observePluginsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<PluginManifestRoom>()
    )

    val environments = observeEnvironmentsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<EnvironmentManifestRoom>()
    )

    val pluginCount = observePluginCountUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val pendingLocalPackageUri = _pendingLocalPackageUri.asStateFlow()
    val isBadgeVisible = _isBadgeVisible.asStateFlow()

    fun setPendingLocalPackageUri(packageUri: Uri){
        _pendingLocalPackageUri.value = packageUri
    }

    fun installLocalPackage(){
        viewModelScope.launch {
            val result = installLocalPackageUseCase(pendingLocalPackageUri.value)
            if (result is PluginError){
                _pluginError.emit(result)
            }else{
                _pluginError.emit(null)
            }
        }
    }
    fun uninstallPlugin(
        pluginManifestRoom: PluginManifestRoom
    ){
        viewModelScope.launch {
            uninstallPluginUseCase(pluginManifestRoom)
        }
    }
    fun uninstallEnvironment(
        environmentManifestRoom: EnvironmentManifestRoom
    ){
        viewModelScope.launch {
            uninstallEnvironmentUseCase(environmentManifestRoom)
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
        pluginManifestRoom: PluginManifestRoom
    ): Uri {
        val shareUri = resolvePluginShareUriUseCase(pluginManifestRoom)
        return shareUri
    }

    fun resolvePluginWebUiUri(
        pluginManifestRoom: PluginManifestRoom
    ): String {
        val webUiUri = resolvePluginWebUiUriUseCase(pluginManifestRoom)
        return webUiUri
    }

    fun resolveEnvironmentShareUri(
        environmentManifestRoom: EnvironmentManifestRoom
    ): Uri {
        val shareUri = resolveEnvironmentShareUriUseCase(environmentManifestRoom)
        return shareUri
    }


}
