package com.baidaidai.rootless_store.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.baidaidai.rootless_store.domain.market.error.MarketError
import com.baidaidai.rootless_store.domain.market.usecase.FetchMarketManifestsUseCase
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifestRemote
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifestRemote
import com.baidaidai.rootless_store.application.environment.InstallEnvironmentFromMarketUseCase
import com.baidaidai.rootless_store.application.plugin.InstallPluginFromMarketUseCase
import com.baidaidai.rootless_store.domain.market.model.MarketManifest
import com.baidaidai.rootless_store.domain.source.model.PluginSource
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
    private val fetchMarketManifestsUseCase: FetchMarketManifestsUseCase,
    private val installPluginFromMarketUseCase: InstallPluginFromMarketUseCase,
    private val installEnvironmentFromMarketUseCase: InstallEnvironmentFromMarketUseCase
): ViewModel() {

    private val _pluginSourceEndpoint = MutableStateFlow<String?>(null)

    // Market errors
    private val _marketError = MutableSharedFlow<MarketError?>()

    val marketError = _marketError.asSharedFlow()

    private val _currentPluginSource = MutableStateFlow<PluginSource?>(null)

    val currentPluginSource = _currentPluginSource.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val marketManifests = _pluginSourceEndpoint
        .filterNotNull()
        .flatMapLatest { pluginSourceEndpoint ->  // Use the latest value

            Log.d("RootlessStoreMarketScreenViewModel._pluginSourceEndpoint",pluginSourceEndpoint)

            fetchMarketManifestsUseCase(pluginSourceEndpoint){ error ->
                // Error Callback Lambda
                _marketError.emit(error)
            }

        }
        // cachedIn 一般放最后，缓存 PagingData 以及其上游变换结果
        .cachedIn(viewModelScope)

    fun setPluginSourceEndpoint(
        pluginSourceEndpoint: String
    ){
        _pluginSourceEndpoint.update { old ->
            if (pluginSourceEndpoint != old){
                Log.d("null2",pluginSourceEndpoint)
                pluginSourceEndpoint
            }else{
                old
            }
        }
    }

    fun selectPluginSource(
        pluginSource: PluginSource
    ){
        _currentPluginSource.update { old ->
            if(pluginSource != old) pluginSource else old
        }

    }

    fun installMarketManifest(marketManifest: MarketManifest){
        viewModelScope.launch {
            when(marketManifest){
                is PluginManifestRemote -> {
                    installPluginFromMarketUseCase(marketManifest.pluginUrl,marketManifest)
                }
                is EnvironmentManifestRemote -> {
                    installEnvironmentFromMarketUseCase(marketManifest.environmentUrl,marketManifest)
                }
                else -> Unit
            }
        }
    }

}
