package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.usecase.AddOneSourceByAuthenticationUseCase
import com.baidaidai.rootless_store.domain.source.usecase.AddOneSourceByDefaultUseCase
import com.baidaidai.rootless_store.domain.source.usecase.DeleteOneSourceUseCase
import com.baidaidai.rootless_store.domain.source.usecase.GetPluginSourceCountUseCase
import com.baidaidai.rootless_store.domain.source.usecase.GetWholeSourceUseCase
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
    getWholeSourceUseCase: GetWholeSourceUseCase,
    getPluginSourceCountUseCase: GetPluginSourceCountUseCase,
    private val addOneSourceByDefaultUseCase: AddOneSourceByDefaultUseCase,
    private val addOneSourceByAuthenticationUseCase: AddOneSourceByAuthenticationUseCase,
    private val deleteOneSourceUseCase: DeleteOneSourceUseCase,
): ViewModel(){

    var latestPluginSourceEndpoint = ""

    val sourceList = getWholeSourceUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val sourceCount = getPluginSourceCountUseCase().stateIn(
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

    fun addOneSourceByDefault(
        sourceURI: String
    ){

        latestPluginSourceEndpoint = sourceURI

        viewModelScope.launch {
            val result = addOneSourceByDefaultUseCase(sourceURI)

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

    fun addOneSourceByAuthentication(userName: String, passWord: String){
        val pluginSourceAuthFormInput = PluginSourceAuthFormInput(
            sourceRemoteEndpoint = latestPluginSourceEndpoint,
            userName = userName,
            passWord = passWord
        )

        viewModelScope.launch {
            val result = addOneSourceByAuthenticationUseCase(pluginSourceAuthFormInput)

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

    fun deleteOneSource(
        pluginSourceInfo: PluginSourceInfo
    ){
        viewModelScope.launch {
            deleteOneSourceUseCase(pluginSourceInfo)
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