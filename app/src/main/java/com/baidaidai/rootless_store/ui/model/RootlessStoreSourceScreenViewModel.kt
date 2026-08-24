package com.baidaidai.rootless_store.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceEvent
import com.baidaidai.rootless_store.domain.source.usecase.AddAuthenticatedPluginSourceUseCase
import com.baidaidai.rootless_store.domain.source.usecase.AddPluginSourceUseCase
import com.baidaidai.rootless_store.domain.source.usecase.DeletePluginSourceUseCase
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
class RootlessStoreSourceScreenViewModel @Inject constructor(
    observePluginSourcesUseCase: ObservePluginSourcesUseCase,
    observePluginSourceCountUseCase: ObservePluginSourceCountUseCase,
    private val addPluginSourceUseCase: AddPluginSourceUseCase,
    private val addAuthenticatedPluginSourceUseCase: AddAuthenticatedPluginSourceUseCase,
    private val deletePluginSourceUseCase: DeletePluginSourceUseCase,
): ViewModel(){

    var latestPluginSourceEndpoint = ""

    val pluginSources = observePluginSourcesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val pluginSourceCount = observePluginSourceCountUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val _sourceError = MutableSharedFlow<PluginSourceEvent.SourceError?>()
    val sourceError = _sourceError.asSharedFlow()

    private val _isDeleteActionVisible = MutableStateFlow(false)
    val isDeleteActionVisible = _isDeleteActionVisible.asStateFlow()

    private val _isAuthenticationDialogVisible = MutableStateFlow(false)
    val isAuthenticationDialogVisible = _isAuthenticationDialogVisible.asStateFlow()

    private val _isAuthenticationSheetVisible = MutableStateFlow(false)
    val isAuthenticationSheetVisible = _isAuthenticationSheetVisible.asStateFlow()

    fun addPluginSource(
        sourceRemoteEndpoint: String
    ){

        latestPluginSourceEndpoint = sourceRemoteEndpoint

        viewModelScope.launch {
            val result = addPluginSourceUseCase(sourceRemoteEndpoint)

            when(result){
                is PluginSourceEvent.SourceError -> {
                    _sourceError.emit(result)
                }
                is PluginSourceEvent.AuthenticationRequired -> {
                    _isAuthenticationDialogVisible.value = true
                }
                is PluginSourceEvent.Success -> {
                    _sourceError.emit(null)
                }
            }
        }
    }

    fun addAuthenticatedPluginSource(username: String, password: String){
        val authenticationInput = PluginSourceAuthenticationInput(
            sourceRemoteEndpoint = latestPluginSourceEndpoint,
            username = username,
            password = password
        )

        viewModelScope.launch {
            val result = addAuthenticatedPluginSourceUseCase(authenticationInput)

            when(result){
                is PluginSourceEvent.SourceError -> {
                    _sourceError.emit(result)
                    cancelSourceAuthentication()
                }
                is PluginSourceEvent.AuthenticationRequired -> {

                }
                is PluginSourceEvent.Success -> {
                    _sourceError.emit(null)
                    cancelSourceAuthentication()
                }
            }
        }
    }

    fun dismissSourceError(){
        viewModelScope.launch {
            _sourceError.emit(null)
        }
    }

    fun deletePluginSource(
        pluginSource: PluginSource
    ){
        viewModelScope.launch {
            deletePluginSourceUseCase(pluginSource)
        }
    }

    fun toggleDeleteActionVisibility(){
        _isDeleteActionVisible.update {
            !it
        }
    }

    fun startSourceAuthentication() {
        _isAuthenticationDialogVisible.value = false
        _isAuthenticationSheetVisible.value = true
    }

    fun cancelSourceAuthentication() {
        _isAuthenticationDialogVisible.value = false
        _isAuthenticationSheetVisible.value = false
    }


}
