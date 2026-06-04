package com.baidaidai.rootless_store.data.shizuku.repository

import IShellService
import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.client.ShizukuAuthManager
import com.baidaidai.rootless_store.data.shizuku.client.ShizukuUserServiceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuAdbRepositoryImpl @Inject constructor(
    private val shizukuUserServiceManager: ShizukuUserServiceManager
) {

    // First Connect time need
    fun getShizukuAuthStatus(): Boolean {
        return ShizukuAuthManager.checkShizukuPermission()
    }

    // If Shizuku isn't Auth, use it
    // Append the check, prevent Double-Auth
    suspend fun getShizukuAuth(): Boolean{
        return if (ShizukuAuthManager.pingShizuku() && !ShizukuAuthManager.checkShizukuPermission()){
             ShizukuAuthManager.activeShizuku()
        } else {
            false
        }
    }

    // Only return ShizukuEndpoint
    // Also return Null, when can't bind shizuku's UserServer
    fun getShizukuEndpoint(): IShellService? {
        return if (connectShizukuEndpoint()){
            Log.d("getShizukuEndpoint","conected shizukuEndpoint")
            Log.d("getShizukuEndpoint",(shizukuUserServiceManager.shizukuEndpoint==null).toString())
            shizukuUserServiceManager.shizukuEndpoint
        }else{

            Log.d("getShizukuEndpoint","fail conected shizukuEndpoint")
            null
        }
    }

    fun getShizukuUserServiceAvailableStatus(): Flow<Boolean> = flow {
        while (true){
            emit(shizukuUserServiceManager.shizukuEndpoint != null)
            delay(3000)
        }
    }

    fun connectShizukuEndpoint(): Boolean{
        return shizukuUserServiceManager.bind()
    }
}