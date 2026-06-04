package com.baidaidai.rootless_store.data.shizuku.client

import IShellService
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointTemplate
import rikka.shizuku.Shizuku
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuUserServiceManager @Inject constructor() {

    var shizukuEndpoint: IShellService? = null
        private set

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shizukuEndpoint = IShellService.Stub.asInterface(binder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shizukuEndpoint = null
        }
    }

    fun bind(): Boolean {
        try{
            val args = Shizuku.UserServiceArgs(
                ComponentName(
                    "com.baidaidai.rootless_store",
                    ShizukuEndpointTemplate::class.java.name
                )
            )
                .processNameSuffix("shell")
                .tag("shell_service")
                .version(1)
                .daemon(true)

            Shizuku.bindUserService(args, connection)
            Log.d("ShizukuEndpointManager","Start to BindUserService")
            Log.d("ShizukuEndpointManager","shizukuEndpoint != null: ${shizukuEndpoint != null}")
            return shizukuEndpoint != null
        }catch (e: Throwable){
            Log.d("errorMessage",e.message.toString())
            Log.d("errorMessage","void")
            return false
        }
    }
}