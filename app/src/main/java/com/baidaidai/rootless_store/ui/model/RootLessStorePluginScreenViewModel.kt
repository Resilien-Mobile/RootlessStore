package com.baidaidai.rootless_store.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.plugin.error.PluginError
import com.baidaidai.rootless_store.domain.plugin.manifest.EnvironmentManifestRoom
import com.baidaidai.rootless_store.domain.plugin.usecase.GetWholePluginInfoUseCase
import com.baidaidai.rootless_store.domain.plugin.usecase.InstallOnePluginUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRoom
import com.baidaidai.rootless_store.domain.plugin.usecase.AbortPluginProcessUseCase
import com.baidaidai.rootless_store.domain.plugin.usecase.UninstallOnePluginUseCase
import com.baidaidai.rootless_store.domain.plugin.usecase.GetPluginInfoCountUseCase
import com.baidaidai.rootless_store.domain.plugin.usecase.GetWholeEnvironmentInfoUseCase
import com.baidaidai.rootless_store.domain.plugin.usecase.SetEnvironmentEnabledUseCase
import com.baidaidai.rootless_store.domain.plugin.usecase.SetPluginEnabledUseCase
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
    private val getWholePluginInfoUseCase: GetWholePluginInfoUseCase,
    private val getWholeEnvironmentInfoUseCase: GetWholeEnvironmentInfoUseCase,
    private val installOnePluginUseCase: InstallOnePluginUseCase,
    private val setPluginEnabledUseCase: SetPluginEnabledUseCase,
    private val setEnvironmentEnabledUseCase: SetEnvironmentEnabledUseCase,
    private val uninstallOnePluginUseCase: UninstallOnePluginUseCase,
    private val abortPluginProcessUseCase: AbortPluginProcessUseCase,
    pluginInfoCountUseCase: GetPluginInfoCountUseCase
): ViewModel() {

//    private val _pluginInfoList = getAllPlugins()  // Will change back to PluginManifestLocal feature
    private val _fileURI = MutableStateFlow<Uri>(value = Uri.EMPTY)
    private val _badgeShowState = MutableStateFlow(false)

    private val _pluginEvent = MutableSharedFlow<PluginError?>()
    val pluginEvent = _pluginEvent.asSharedFlow()

    val pluginInfoList = getWholePluginInfoUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<PluginManifestRoom>()
    )

    val environmentInfoList = getWholeEnvironmentInfoUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<EnvironmentManifestRoom>()
    )

    val pluginInfoCount = pluginInfoCountUseCase().stateIn(
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
            val result = installOnePluginUseCase(fileURI.value)
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
            uninstallOnePluginUseCase(pluginManifestRoom)
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

//    private fun getAllPlugins() {
//        return viewModelScope.launch{
//            return@launch  = getWholePluginInfoUseCase().stateIn()
//        }.await()
//    }


}