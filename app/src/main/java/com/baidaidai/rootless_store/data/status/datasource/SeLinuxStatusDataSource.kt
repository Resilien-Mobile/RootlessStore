package com.baidaidai.rootless_store.data.status.datasource

import android.util.Log
import com.baidaidai.rootless_store.domain.status.model.SeLinuxStatus
import com.topjohnwu.superuser.Shell
import javax.inject.Inject

class SeLinuxStatusDataSource @Inject constructor() {
    fun getSeLinuxStatus(): SeLinuxStatus{
        val shell = Shell.Builder.create()
            .setFlags(Shell.FLAG_REDIRECT_STDERR)
            .build("sh")

        val outputLines = ArrayList<String>()
        val commandResult = shell.use {
            it.newJob().add("getenforce").to(outputLines, outputLines).exec()
        }
        val commandOutput = commandResult.out.joinToString("\n").trim()

        if (commandResult.isSuccess) {
            return when (commandOutput) {
                "Enforcing" -> SeLinuxStatus.Enforcing
                "Permissive" -> SeLinuxStatus.Permissive
                "Disabled" -> SeLinuxStatus.Disabled
                else -> SeLinuxStatus.Unknown
            }
        }

        return if (commandOutput.endsWith("Permission denied")) {
            SeLinuxStatus.Enforcing
        } else {
            Log.d("err",commandResult.err.isEmpty().toString())
            Log.d("out",commandResult.out.isEmpty().toString())
            SeLinuxStatus.Unknown
        }
    }
}
