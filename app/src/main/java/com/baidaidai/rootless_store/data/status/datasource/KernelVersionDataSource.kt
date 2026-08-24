package com.baidaidai.rootless_store.data.status.datasource

import com.topjohnwu.superuser.Shell
import javax.inject.Inject

class KernelVersionDataSource @Inject constructor(

) {
    fun getKernelVersion(): String{
        val process = ProcessBuilder("sh", "-c", "uname -r")
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }

        process.waitFor()

        return output.trim().substringBefore("-")
    }
}
