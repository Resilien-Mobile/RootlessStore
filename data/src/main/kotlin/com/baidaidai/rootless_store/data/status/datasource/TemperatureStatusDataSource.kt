package com.baidaidai.rootless_store.data.status.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.os.PowerManager
import com.baidaidai.rootless_store.domain.status.model.TemperatureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import androidx.core.content.ContextCompat

class TemperatureStatusDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun observeDeviceTemperatureStatus(): Flow<TemperatureStatus> = callbackFlow {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val executor = ContextCompat.getMainExecutor(context)

        val listener = PowerManager.OnThermalStatusChangedListener { thermalStatus ->
            val temperatureStatus = when (thermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> TemperatureStatus.NORMAL
                PowerManager.THERMAL_STATUS_LIGHT -> TemperatureStatus.LOW
                PowerManager.THERMAL_STATUS_MODERATE -> TemperatureStatus.NORMAL
                PowerManager.THERMAL_STATUS_SEVERE -> TemperatureStatus.HOT
                PowerManager.THERMAL_STATUS_CRITICAL -> TemperatureStatus.CRITICAL
                PowerManager.THERMAL_STATUS_EMERGENCY -> TemperatureStatus.CRITICAL
                PowerManager.THERMAL_STATUS_SHUTDOWN -> TemperatureStatus.SHUTDOWN
                else -> TemperatureStatus.ERROR
            }

            trySend(temperatureStatus)
        }

        powerManager.addThermalStatusListener(executor, listener)

        awaitClose {
            powerManager.removeThermalStatusListener(listener)
        }
    }
}
