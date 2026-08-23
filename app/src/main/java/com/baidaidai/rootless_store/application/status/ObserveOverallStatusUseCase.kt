package com.baidaidai.rootless_store.application.status

import android.util.Log
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuPermissionAndAuthGatewayImpl
import com.baidaidai.rootless_store.data.status.repository.StoreStatusRepositoryImpl
import com.baidaidai.rootless_store.domain.status.model.HosterOverallStatus
import com.baidaidai.rootless_store.domain.status.model.SELinuxStatus
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ObserveOverallStatusUseCase @Inject constructor(
    private val storeStatusRepositoryImpl: StoreStatusRepositoryImpl,
    private val shizukuPermissionAndAuthGatewayImpl: ShizukuPermissionAndAuthGatewayImpl,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<HosterOverallStatus> = flow {

        while (true){

            val enableChooserPreference = storeStatusRepositoryImpl.observeEnableChooserPreference().first()
            val isRoot = Shell.getShell().isRoot
            val isShizukuAvailable =
                if (!isRoot && shizukuPermissionAndAuthGatewayImpl.pingShizuku() && shizukuPermissionAndAuthGatewayImpl.checkShizukuPermission()) {
                    shizukuUserServiceGatewayImpl.tryBindShizukuUserService()
                    true
                } else {
                    false
                }
            val seLinuxStatus = storeStatusRepositoryImpl.getSELinuxStatus()

            val executionContextPreference = if (enableChooserPreference) storeStatusRepositoryImpl.observeExecutionContextPreference().first() else null

            val status = when {
                (executionContextPreference != null) -> {
                    Log.d("HosterOverallStatus", "Now At ContextPreference")
                    Log.d("HosterOverallStatus", executionContextPreference.toString())
                    executionContextPreference
                }
                isRoot -> {
                    Log.d("HosterOverallStatus", "Root")
                    HosterOverallStatus.ROOTD
                }
                isShizukuAvailable -> {
                    Log.d("HosterOverallStatus", "Shizuku")
                    HosterOverallStatus.ADB
                }
                seLinuxStatus == SELinuxStatus.Permissive -> {
                    Log.d("HosterOverallStatus", "Permissive")
                    HosterOverallStatus.PERMISSIVE
                }
                else -> {
                    Log.d("HosterOverallStatus", "Limited")
                    HosterOverallStatus.LIMITED
                }
            }

            emit(status)
            delay(3000.milliseconds)
        }
    }
}
