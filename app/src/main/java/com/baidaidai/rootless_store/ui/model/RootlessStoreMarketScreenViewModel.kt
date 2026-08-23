package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.market.usecase.FetchRemotePluginsUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.application.environment.InstallEnvironmentFromMarketUseCase
import com.baidaidai.rootless_store.application.plugin.InstallPluginFromMarketUseCase
import com.baidaidai.rootless_store.domain.module.model.ModuleManifestCollection
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootlessStoreMarketScreenViewModel @Inject constructor(
    private val fetchRemotePluginsUseCase: FetchRemotePluginsUseCase,
    private val installPluginFromMarketUseCase: InstallPluginFromMarketUseCase,
    private val installEnvironmentFromMarketUseCase: InstallEnvironmentFromMarketUseCase
): ViewModel() {

    /**
     *  ClickSourceList
     *  ⬇️
     *  updatePluginSourceUri
     *  ⬇️
     *  navigate("MarketScreen")
     *  ⬇️
     *  remotePluginList
     */

    private var _pluginSourceUri = MutableStateFlow<String?>(null)

    // Market Error Handler
    private val _marketEvent = MutableSharedFlow<MarketError?>()

    val marketEvent = _marketEvent.asSharedFlow()

    private val _currentPluginSourceInfo = MutableStateFlow<PluginSourceInfo?>(null)

    val currentPluginSource = _currentPluginSourceInfo.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val remoteModuleList = _pluginSourceUri
        .filterNotNull()
        .flatMapLatest { pluginSourceUri ->  // Use the latest value

            Log.d("RootlessStoreMarketScreenViewModel._pluginSourceUri",pluginSourceUri)

            fetchRemotePluginsUseCase(pluginSourceUri){ marketError ->
                // Error Callback Lambda
                _marketEvent.emit(marketError)
            }

        }
        // cachedIn 一般放最后，缓存 PagingData 以及其上游变换结果
        .cachedIn(viewModelScope)

    fun updatePluginSourceUri(
        pluginSourceUri: String
    ){
        _pluginSourceUri.update { old ->
            if (pluginSourceUri != old){
                Log.d("null2",pluginSourceUri)
                pluginSourceUri
            }else{
                old
            }
        }
    }

    fun updateCurrentPluginSource(
        pluginSourceInfo: PluginSourceInfo
    ){
        _currentPluginSourceInfo.update { old ->
            if(pluginSourceInfo != old) pluginSourceInfo else old
        }

    }

    fun installModule(moduleManifest: ModuleManifestCollection){
        viewModelScope.launch {
            when(moduleManifest){
                is PluginManifestRemote -> {
                    installPluginFromMarketUseCase(moduleManifest.pluginUri,moduleManifest)
                }
                is EnvironmentManifestRemote -> {
                    installEnvironmentFromMarketUseCase(moduleManifest.environmentUri,moduleManifest)
                }
                else -> Unit
            }
        }
    }

}
