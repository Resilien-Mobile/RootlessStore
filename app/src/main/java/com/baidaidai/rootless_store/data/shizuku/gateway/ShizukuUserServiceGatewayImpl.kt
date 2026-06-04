package com.baidaidai.rootless_store.data.shizuku.gateway

import com.baidaidai.rootless_store.data.shizuku.client.ShizukuUserServiceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShizukuUserServiceGatewayImpl @Inject constructor(
    private val shizukuUserServiceManager: ShizukuUserServiceManager
) {
    fun getShizukuUserServiceAvailableStatus(): Flow<Boolean> = flow {
        while (true){
            emit(shizukuUserServiceManager.shizukuEndpoint != null)
            delay(3000)
        }
    }
}