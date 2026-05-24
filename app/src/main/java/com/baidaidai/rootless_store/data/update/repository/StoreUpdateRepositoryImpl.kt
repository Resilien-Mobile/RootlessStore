package com.baidaidai.rootless_store.data.update.repository

import com.baidaidai.rootless_store.data.update.gateway.StoreUpdateGatewayImpl
import javax.inject.Inject

class StoreUpdateRepositoryImpl @Inject constructor(
    private val storeUpdateGatewayImpl: StoreUpdateGatewayImpl
){
    suspend fun getLatestVersion(): String? = storeUpdateGatewayImpl.getLatestVersion()
}