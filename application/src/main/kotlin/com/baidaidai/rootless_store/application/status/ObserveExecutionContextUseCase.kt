package com.baidaidai.rootless_store.application.status

import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionGatewayImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ObserveExecutionContextUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl,
    private val shizukuPermissionGatewayImpl: ShizukuPermissionGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<ExecutionContext> = flow {

        while (true){

            val isExecutionContextChooserEnabled = storeStatusRepositoryImpl.observeExecutionContextChooserEnabled().first()
            val isRoot = Shell.getShell().isRoot
            val isShizukuAvailable =
                if (!isRoot && shizukuPermissionGatewayImpl.pingShizuku() && shizukuPermissionGatewayImpl.hasShizukuPermission()) {
                    shizukuUserServiceGatewayImpl.startShizukuUserService()
                    true
                } else {
                    false
                }
            val seLinuxStatus = storeStatusRepositoryImpl.getSeLinuxStatus()

            val executionContextPreference = if (isExecutionContextChooserEnabled) storeStatusRepositoryImpl.observeExecutionContextPreference().first() else null

            val executionContext = when {
                (executionContextPreference != null) -> {
                    Log.d("ExecutionContext", "Now At ContextPreference")
                    Log.d("ExecutionContext", executionContextPreference.toString())
                    executionContextPreference
                }
                isRoot -> {
                    Log.d("ExecutionContext", "Root")
                    ExecutionContext.ROOTD
                }
                isShizukuAvailable -> {
                    Log.d("ExecutionContext", "Shizuku")
                    ExecutionContext.ADB
                }
                seLinuxStatus == SeLinuxStatus.Permissive -> {
                    Log.d("ExecutionContext", "Permissive")
                    ExecutionContext.PERMISSIVE
                }
                else -> {
                    Log.d("ExecutionContext", "Limited")
                    ExecutionContext.LIMITED
                }
            }

            emit(executionContext)
            delay(3000.milliseconds)
        }
    }
}
