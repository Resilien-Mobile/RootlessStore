package com.baidaidai.rootless_store.data.shizuku.server

import IShellCallback
import android.util.Log

internal class ShizukuEndpointTemplate : IShellService.Stub() {

    override fun exec(pluginExecuteEntryPoint: String,pluginPackageDirectory: String,callback: IShellCallback){
        val process = ProcessBuilder("run-as","com.baidaidai.rootless_store","sh","-c","cd $pluginPackageDirectory ;echo PID:$$; exec $pluginExecuteEntryPoint").start()

        process
            .inputStream
            .bufferedReader()
            .useLines{ line ->
                line.forEach {
                    callback.onExecute(it)
                }
            }

        process
            .errorStream
            .bufferedReader()
            .useLines{ line ->
                line.forEach {
                    callback.onError(it)
                }
            }
    }

    override fun kill(progressPid: Int): Boolean {

        val process = ProcessBuilder(
            "run-as","com.baidaidai.rootless_store","sh","-c","kill ${progressPid.toString()}"
        )
            .redirectErrorStream(true)
            .start()
            .waitFor()
        return process == 0
    }

    override fun command(commandContent: String, callback: IShellCallback ){
        val process = ProcessBuilder("sh","-c",commandContent).start()

        process
            .inputStream
            .bufferedReader()
            .useLines{ line ->
                line.forEach {
                    callback.onExecute(it)
                }
            }

        process
            .errorStream
            .bufferedReader()
            .useLines{ line ->
                line.forEach {
                    callback.onError(it)
                }
            }
    }
}