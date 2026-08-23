package com.baidaidai.rootless_store.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.environment.ResolveEnvironmentShareUriUseCase
import com.baidaidai.rootless_store.application.module.InstallModuleUseCase
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.application.plugin.ObservePluginsUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.application.plugin.AbortPluginProcessUseCase
import com.baidaidai.rootless_store.application.plugin.UninstallPluginUseCase
import com.baidaidai.rootless_store.application.plugin.ObservePluginCountUseCase
import com.baidaidai.rootless_store.application.environment.ObserveEnvironmentsUseCase
import com.baidaidai.rootless_store.application.environment.SetEnvironmentEnabledUseCase
import com.baidaidai.rootless_store.application.plugin.SetPluginEnabledUseCase
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
class RootLessStorePluginScreenViewModel @Inject constructor(
    private val observePluginsUseCase: ObservePluginsUseCase,
    private val observeEnvironmentsUseCase: ObserveEnvironmentsUseCase,
    private val installModuleUseCase: InstallModuleUseCase,
    private val setPluginEnabledUseCase: SetPluginEnabledUseCase,
    private val setEnvironmentEnabledUseCase: SetEnvironmentEnabledUseCase,
    private val uninstallPluginUseCase: UninstallPluginUseCase,
    private val uninstallEnvironmentUseCase: UninstallEnvironmentUseCase,
    private val abortPluginProcessUseCase: AbortPluginProcessUseCase,
    private val resolvePluginShareUriUseCase: ResolvePluginShareUriUseCase,
    private val resolvePluginWebUiUriUseCase: ResolvePluginWebUiUriUseCase,
    private val resolveEnvironmentShareUriUseCase: ResolveEnvironmentShareUriUseCase,
    observePluginCountUseCase: ObservePluginCountUseCase
): ViewModel() {

//    private val _pluginInfoList = observePlugins()  // Will change back to PluginManifestLocal feature
    private val _fileUri = MutableStateFlow<Uri>(value = Uri.EMPTY)
    private val _badgeShowState = MutableStateFlow(false)

    private val _pluginEvent = MutableSharedFlow<PluginError?>()
    val pluginEvent = _pluginEvent.asSharedFlow()

    val pluginInfoList = observePluginsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<PluginManifestRoom>()
    )

    val environmentInfoList = observeEnvironmentsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<EnvironmentManifestRoom>()
    )

    val pluginInfoCount = observePluginCountUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val fileUri = _fileUri.asStateFlow()
    val badgeShowState = _badgeShowState.asStateFlow()

    fun updateFileUri(uri: Uri){
        _fileUri.value = uri
    }

    fun installPlugin(){
        viewModelScope.launch {
            val result = installModuleUseCase(fileUri.value)
            if (result is PluginError){
                _pluginEvent.emit(result)
            }else{
                _pluginEvent.emit(null)
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
            setPluginEnabledUseCase(
                pluginId = pluginId,
                isEnabled = isEnabled
            )
        }
    }

    fun setEnvironmentEnabled(
        environmentId: String,
        isEnabled: Boolean
    ){
        viewModelScope.launch {
            setEnvironmentEnabledUseCase(
                environmentId = environmentId,
                isEnabled = isEnabled
            )
        }
    }

    fun changeBadgeShowStatus(){
        _badgeShowState.update { !it }
    }

    fun resolvePluginShareUri(
        pluginManifestRoom: PluginManifestRoom
    ): Uri {
        val shareLink = resolvePluginShareUriUseCase(pluginManifestRoom)
        return shareLink
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
        val shareLink = resolveEnvironmentShareUriUseCase(environmentManifestRoom)
        return shareLink
    }


}
