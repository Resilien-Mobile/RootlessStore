package com.baidaidai.rootless_store.data.shizuku.server

import IShellCallback
import android.util.Log

internal class ShizukuEndpointTemplate : IShellService.Stub() {

    override fun exec(
        pluginExecuteEntryPoint: String,
        pluginPackageDirectory: String,
        callback: IShellCallback,
        environmentPATH: String,
        environmentLDPATH: String,
        environmentConfigKeyList: MutableList<String>,
        environmentConfigValueList: MutableList<String>
    ) {
        val processBuilder = ProcessBuilder(
            "run-as",
            "com.baidaidai.rootless_store",
            "sh",
            "-c",
            """
                cd ${pluginPackageDirectory.shellQuote()}
                export PATH="$environmentPATH:${'$'}PATH"
                export LD_LIBRARY_PATH="$environmentLDPATH:${'$'}LD_LIBRARY_PATH"
                ${buildEnvironmentConfigExportString(
                    environmentConfigKeyList = environmentConfigKeyList,
                    environmentConfigValueList = environmentConfigValueList
                )}
                echo PID:${'$'}$
                exec $pluginExecuteEntryPoint
            """.trimIndent()
        )

        val process = processBuilder.start()

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

    override fun command(
        commandContent: String,
        environmentPATH: String,
        environmentLDPATH: String,
        environmentConfigKeyList: MutableList<String>,
        environmentConfigValueList: MutableList<String>,
        callback: IShellCallback,
        useRunAs: Boolean
    ) {
        val processBuilder = ProcessBuilder(
            buildList {
                if (useRunAs) {
                    add("run-as")
                    add("com.baidaidai.rootless_store")
                }
                add("sh")
                add("-c")
                add(
                    """
                    export PATH="$environmentPATH:${'$'}PATH"
                    export LD_LIBRARY_PATH="$environmentLDPATH:${'$'}LD_LIBRARY_PATH"
                    ${buildEnvironmentConfigExportString(
                            environmentConfigKeyList = environmentConfigKeyList,
                            environmentConfigValueList = environmentConfigValueList
                    )}
                    $commandContent
                """.trimIndent()
                )
            }
        )

        Log.d("ShizukuEndpointTemplate","environmentPATH: $environmentPATH")
        Log.d("ShizukuEndpointTemplate","environmentLDPATH: $environmentLDPATH")

        val process = processBuilder.start()

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

    private fun buildEnvironmentConfigExportString(
        environmentConfigKeyList: List<String>,
        environmentConfigValueList: List<String>
    ): String {
        return environmentConfigKeyList
            .zip(environmentConfigValueList)
            .filter { (key, _) -> key.isValidShellVariableName() }
            .joinToString(separator = "; ") { (key, value) ->
                "export $key=${value.shellQuote()}"
            }
    }

    private fun String.isValidShellVariableName(): Boolean {
        return matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))
    }

    private fun String.shellQuote(): String {
        return "'${replace("'", "'\"'\"'")}'"
    }

}