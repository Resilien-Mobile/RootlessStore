package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.usecase.AddSourceByAuthenticationUseCase
import com.baidaidai.rootless_store.domain.source.usecase.AddSourceByDefaultUseCase
import com.baidaidai.rootless_store.domain.source.usecase.DeleteSourceUseCase
import com.baidaidai.rootless_store.domain.source.usecase.ObservePluginSourceCountUseCase
import com.baidaidai.rootless_store.domain.source.usecase.ObservePluginSourcesUseCase
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

@HiltViewModel
class RootLessStoreSourceScreenViewModel @Inject constructor(
    observePluginSourcesUseCase: ObservePluginSourcesUseCase,
    observePluginSourceCountUseCase: ObservePluginSourceCountUseCase,
    private val addSourceByDefaultUseCase: AddSourceByDefaultUseCase,
    private val addSourceByAuthenticationUseCase: AddSourceByAuthenticationUseCase,
    private val deleteSourceUseCase: DeleteSourceUseCase,
): ViewModel(){

    var latestPluginSourceEndpoint = ""

    val sourceList = observePluginSourcesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val sourceCount = observePluginSourceCountUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val _sourceEvent = MutableSharedFlow<PluginSourceEvent.SourceError?>()
    val sourceEvent = _sourceEvent.asSharedFlow()

    private val _deleterShowStatus = MutableStateFlow(false)
    val deleterShowStatus = _deleterShowStatus.asStateFlow()

    private val _authenticationAlertDialogShowStatus = MutableStateFlow(false)
    val authenticationAlertDialogShowStatus = _authenticationAlertDialogShowStatus.asStateFlow()

    private val _authenticationBottomSheetShowStatus = MutableStateFlow(false)
    val authenticationBottomSheetShowStatus = _authenticationBottomSheetShowStatus.asStateFlow()

    fun addSourceByDefault(
        sourceUri: String
    ){

        latestPluginSourceEndpoint = sourceUri

        viewModelScope.launch {
            val result = addSourceByDefaultUseCase(sourceUri)

            when(result){
                is PluginSourceEvent.SourceError -> {
                    _sourceEvent.emit(result)
                }
                is PluginSourceEvent.SourceAuthentication -> {
                    _authenticationAlertDialogShowStatus.value = true
                }
                is PluginSourceEvent.Success -> {
                    _sourceEvent.emit(null)
                }
            }
        }
    }

    fun addSourceByAuthentication(userName: String, passWord: String){
        val pluginSourceAuthFormInput = PluginSourceAuthFormInput(
            sourceRemoteEndpoint = latestPluginSourceEndpoint,
            userName = userName,
            passWord = passWord
        )

        viewModelScope.launch {
            val result = addSourceByAuthenticationUseCase(pluginSourceAuthFormInput)

            when(result){
                is PluginSourceEvent.SourceError -> {
                    _sourceEvent.emit(result)
                    cancelSourceAuthentication()
                }
                is PluginSourceEvent.SourceAuthentication -> {

                }
                is PluginSourceEvent.Success -> {
                    _sourceEvent.emit(null)
                    cancelSourceAuthentication()
                }
            }
        }
    }

    fun onOkButtonClick(){
        viewModelScope.launch {
            _sourceEvent.emit(null)
        }
    }

    fun deleteSource(
        pluginSourceInfo: PluginSourceInfo
    ){
        viewModelScope.launch {
            deleteSourceUseCase(pluginSourceInfo)
        }
    }

    fun changeDeleterShowStatus(){
        _deleterShowStatus.update {
            !it
        }
    }

    fun startSourceAuthentication() {
        _authenticationAlertDialogShowStatus.value = false
        _authenticationBottomSheetShowStatus.value = true
    }

    fun cancelSourceAuthentication() {
        _authenticationAlertDialogShowStatus.value = false
        _authenticationBottomSheetShowStatus.value = false
    }


}
