package com.baidaidai.rootless_store.data.status.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.os.PowerManager
import com.baidaidai.rootless_store.domain.status.model.TempStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import androidx.core.content.ContextCompat

class TemperatureStatusDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun observeDeviceTemperatureStatus(): Flow<TempStatus> = callbackFlow {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val executor = ContextCompat.getMainExecutor(context)

        val listener = PowerManager.OnThermalStatusChangedListener { status ->
            val tempStatus = when (status) {
                PowerManager.THERMAL_STATUS_NONE -> TempStatus.NORMAL
                PowerManager.THERMAL_STATUS_LIGHT -> TempStatus.LOW
                PowerManager.THERMAL_STATUS_MODERATE -> TempStatus.NORMAL
                PowerManager.THERMAL_STATUS_SEVERE -> TempStatus.HOT
                PowerManager.THERMAL_STATUS_CRITICAL -> TempStatus.CRITICAL
                PowerManager.THERMAL_STATUS_EMERGENCY -> TempStatus.CRITICAL
                PowerManager.THERMAL_STATUS_SHUTDOWN -> TempStatus.SHUTDOWN
                else -> TempStatus.ERROR
            }

            trySend(tempStatus)
        }

        powerManager.addThermalStatusListener(executor, listener)

        awaitClose {
            powerManager.removeThermalStatusListener(listener)
        }
    }
}
