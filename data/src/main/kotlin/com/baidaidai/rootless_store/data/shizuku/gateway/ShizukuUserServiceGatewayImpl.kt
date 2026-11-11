package com.baidaidai.rootless_store.data.shizuku.gateway

import IShellService
import IProcProcessScanner
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.baidaidai.rootless_store.data.process.shizuku.ProcProcessScanner
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointTemplate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rikka.shizuku.Shizuku
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class ShizukuUserServiceGatewayImpl @Inject constructor() {

    private var shizukuUserService: IShellService? = null
    private var procProcessScannerUserService: IProcProcessScanner? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shizukuUserService = IShellService.Stub.asInterface(binder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shizukuUserService = null
        }
    }

    private val procProcessScannerConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {

            procProcessScannerUserService = IProcProcessScanner.Stub.asInterface(binder)

        }

        override fun onServiceDisconnected(name: ComponentName?) {

            procProcessScannerUserService = null

        }

    }

    fun startShizukuUserService() {
        val args = Shizuku.UserServiceArgs(
            ComponentName(
                "com.baidaidai.rootless_store",
                ShizukuEndpointTemplate::class.java.name
            )
        )
            .processNameSuffix("shell")
            .tag("shell_service")
            .version(6)
            .daemon(true)

        Shizuku.bindUserService(args, connection)
        Log.d("ShizukuEndpointManager","Start to BindUserService")
        Log.d("ShizukuEndpointManager","shizukuEndpoint != null: ${shizukuUserService != null}")
    }

    fun findShizukuUserService(): IShellService? {
        return shizukuUserService
    }

    fun startProcProcessScannerUserService() {

        val args = Shizuku.UserServiceArgs(
            ComponentName(
                "com.baidaidai.rootless_store",
                ProcProcessScanner::class.java.name
            )
        )
            .processNameSuffix("proc_process_scanner")
            .tag("proc_process_scanner_service")
            .version(2)
            .daemon(true)

        Shizuku.bindUserService(args, procProcessScannerConnection)
        Log.d(
            "ShizukuEndpointManager",
            "Start to bind ProcProcessScanner UserService"
        )

    }

    fun findProcProcessScannerUserService(): IProcProcessScanner? {

        return procProcessScannerUserService

    }

    fun observeProcProcessScannerUserServiceAvailability(): Flow<Boolean> = flow {

        while (true) {

            emit(procProcessScannerUserService != null)
            delay(3000.milliseconds)

        }

    }

    fun observeShizukuUserServiceAvailability(): Flow<Boolean> = flow {
        while (true){
            emit(shizukuUserService != null)
            delay(3000.milliseconds)
        }
    }

}
