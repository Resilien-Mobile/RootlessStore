package com.baidaidai.rootless_store.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.environment.GetEnvironmentShareLinkUseCase
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
import com.baidaidai.rootless_store.application.plugin.GetPluginShareLinkUseCase
import com.baidaidai.rootless_store.application.plugin.GetPluginWebUiUriUseCase
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
    private val getPluginShareLinkUseCase: GetPluginShareLinkUseCase,
    private val getPluginWebUiUriUseCase: GetPluginWebUiUriUseCase,
    private val getEnvironmentShareLinkUseCase: GetEnvironmentShareLinkUseCase,
    observePluginCountUseCase: ObservePluginCountUseCase
): ViewModel() {

//    private val _pluginInfoList = observePlugins()  // Will change back to PluginManifestLocal feature
    private val _fileURI = MutableStateFlow<Uri>(value = Uri.EMPTY)
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

    val fileURI = _fileURI.asStateFlow()
    val badgeShowState = _badgeShowState.asStateFlow()

    fun updateFileURI(uri: Uri){
        _fileURI.value = uri
    }

    fun installPlugin(){
        viewModelScope.launch {
            val result = installModuleUseCase(fileURI.value)
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
        pluginID: String,
        pluginEnabledStatus: Boolean
    ){
        viewModelScope.launch {
            setPluginEnabledUseCase(
                pluginID = pluginID,
                pluginEnabledStatus = pluginEnabledStatus
            )
        }
    }

    fun setEnvironmentEnabled(
        environmentID: String,
        environmentEnabledStatus: Boolean
    ){
        viewModelScope.launch {
            setEnvironmentEnabledUseCase(
                environmentID = environmentID,
                environmentEnabledStatus = environmentEnabledStatus
            )
        }
    }

    fun changeBadgeShowStatus(){
        _badgeShowState.update { !it }
    }

    fun getPluginShareLink(
        pluginManifestRoom: PluginManifestRoom
    ): Uri {
        val shareLink = getPluginShareLinkUseCase(pluginManifestRoom)
        return shareLink
    }

    fun getPluginWebUiUri(
        pluginManifestRoom: PluginManifestRoom
    ): String {
        val webUiUri = getPluginWebUiUriUseCase(pluginManifestRoom)
        return webUiUri
    }

    fun getEnvironmentShareLink(
        environmentManifestRoom: EnvironmentManifestRoom
    ): Uri {
        val shareLink = getEnvironmentShareLinkUseCase(environmentManifestRoom)
        return shareLink
    }


}
