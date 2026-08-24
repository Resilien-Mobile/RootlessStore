package com.baidaidai.rootless_store.data.status.datasource

import android.content.Context
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StorageStatusDataSource @Inject constructor(
    @ApplicationContext appContext: Context
){
    private val statFs = StatFs(appContext.dataDir.absolutePath)
    fun getUsedStorage(): Double{
        val usedStorageBytes: Long = statFs.totalBytes - statFs.availableBytes
        return bytesToGibibytes(usedStorageBytes)
    }
    fun getTotalStorage(): Double{
        val totalStorageBytes: Long = statFs.totalBytes
        return bytesToGibibytes(totalStorageBytes)
    }

    private fun bytesToGibibytes(bytes: Long): Double {
        return bytes / (1024.0 * 1024.0 * 1024.0)
    }
}
