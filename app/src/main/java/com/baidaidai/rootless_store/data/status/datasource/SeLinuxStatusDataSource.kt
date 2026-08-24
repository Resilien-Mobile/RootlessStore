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

        val list = ArrayList<String>()
        val result = shell.use {
            it.newJob().add("getenforce").to(list, list).exec()
        }
        val output = result.out.joinToString("\n").trim()

        if (result.isSuccess) {
            return when (output) {
                "Enforcing" -> SeLinuxStatus.Enforcing
                "Permissive" -> SeLinuxStatus.Permissive
                "Disabled" -> SeLinuxStatus.Disabled
                else -> SeLinuxStatus.Unknown
            }
        }

        return if (output.endsWith("Permission denied")) {
            SeLinuxStatus.Enforcing
        } else {
            Log.d("err",result.err.isEmpty().toString())
            Log.d("out",result.out.isEmpty().toString())
            SeLinuxStatus.Unknown
        }
    }
}
