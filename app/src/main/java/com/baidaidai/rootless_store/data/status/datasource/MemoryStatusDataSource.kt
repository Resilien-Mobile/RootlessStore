package com.baidaidai.rootless_store.data.status.datasource

import android.app.ActivityManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MemoryStatusDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private fun getCurrentMemoryInfo(): ActivityManager.MemoryInfo {
        return ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
    }

    fun getTotalMemory(): Double{
        val totalRamBytes = getCurrentMemoryInfo().totalMem
        return bytesToGibibytes(totalRamBytes)
    }
    fun getUsedMemory(): Double{
        val usedRamBytes = getCurrentMemoryInfo().totalMem - getCurrentMemoryInfo().availMem
        return bytesToGibibytes(usedRamBytes)
    }
    private fun bytesToGibibytes(bytes: Long): Double {
        return bytes / (1024.0 * 1024.0 * 1024.0)
    }
}
