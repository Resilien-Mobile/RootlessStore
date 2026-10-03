package com.baidaidai.rootless_store.data.execution.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.monitor.PluginProcessMonitor
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResult
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResultTag
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class PluginExecutionGatewayImpl @Inject constructor(
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val pluginProcessMonitor: PluginProcessMonitor
) {

    fun executePluginEntryPoint(
        pluginEntryPoint: String,
        pluginPackageDirectory: String,
        shouldMonitor: Boolean = false
    ): Flow<ExecutionResult> = callbackFlow {

        // Prepare Commands
        val command = commandFactory(
            shellExecutable = resolveLocalShellExecutable(),
            pluginEntryPoint = pluginEntryPoint,
            pluginPackageDirectory = pluginPackageDirectory
        )
        val processBuilder = ProcessBuilder(command)

        // Prepare Environments
        val environment = processBuilder.environment()

        val oldPath = environment["PATH"].orEmpty()
        val oldLdPath = environment["LD_LIBRARY_PATH"].orEmpty()

        val environmentPath = environmentRepositoryImpl.resolveEnvironmentRuntimePath()
        val environmentLdPath = environmentRepositoryImpl.resolveEnvironmentLdPath()
        val environmentConfig = environmentRepositoryImpl.resolveEnvironmentConfig()

        environment["PATH"] = "$environmentPath:$oldPath"
        environment["LD_LIBRARY_PATH"] = "$environmentLdPath:$oldLdPath"
        environment.putAll(environmentConfig)

        Log.d("executePluginEntryPoint","environmentPath: $environmentPath")
        Log.d("executePluginEntryPoint","environmentLdPath: $environmentLdPath")

        // Start Process and
        // register monitor
        val process = processBuilder.start()
        if (shouldMonitor){
            pluginProcessMonitor(process)
        }

        launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Normal,
                            output = "- ${result.toString()}"
                        )
                    )
                }
            }
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { error ->
                    send(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Normal,
                            output = "- ${error.toString()}"
                        )
                    )
                }
            }
        }

        awaitClose {
        }

    }.flowOn(Dispatchers.IO)

    fun executePluginByShizuku(
        pluginDirectory: String,
        pluginEntryPoint: String,
        shouldMonitor: Boolean
    ): Flow<ExecutionResult> = callbackFlow {
        launch(Dispatchers.IO) {
            val callback = ShizukuEndpointCallback(
                onOutput = { output ->
                    trySend(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Normal,
                            output = "- ${output.toString()}"
                        )
                    )
                },
                onErrors = { error ->
                    trySend(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Error,
                            output = "- ${error.toString()}"
                        )
                    )
                },
                onProcessExit = { exitCode ->
                    pluginProcessMonitor(exitCode)
                }
            )

            shizukuUserServiceGatewayImpl
                .findShizukuUserService()
                ?.exec(pluginDirectory,pluginEntryPoint,shouldMonitor,callback)
        }
        awaitClose {  }
    }

    fun abortPluginProcess(pluginProcessPid: Int?){
        if (pluginProcessPid != null){
            ProcessBuilder(
                resolveLocalShellExecutable(), "-c", "kill -9 $pluginProcessPid"
            ).start()
        }
    }

    fun abortPluginProcessByShizuku(pluginProcessPid: Int?): Boolean{
        return if (pluginProcessPid != null){
            Log.d("exam","shizuku ${shizukuUserServiceGatewayImpl.findShizukuUserService() == null}")
            Log.d("pid","$pluginProcessPid")

            val processAbortResult = shizukuUserServiceGatewayImpl.findShizukuUserService()
                ?.kill(pluginProcessPid)

            Log.d("kill pid result",processAbortResult.toString())

            processAbortResult != null
        }else{
            false
        }
    }

    /**
     * Shizuku 是例外，因为普通 process 无法直接调用
     *
     *需要通过 Binder, 所以判断也没有意义，这里不做判断
     */
    fun resolveLocalShellExecutable(): String{
        val shell = Shell.getShell()
        return if (shell.isRoot){
            "su"
        }else{
            "sh"
        }
    }

    /**
     * 构造供 [ProcessBuilder] 使用的命令参数列表。
     *
     * 该方法只负责根据传入的 Shell、插件入口点以及插件目录组装命令，
     * 不负责启动进程或配置运行环境。
     *
     * 最终返回的参数列表通常形如：`sh -c cd <pluginPackageDirectory> ; echo PID:$$ ; exec <pluginEntryPoint>`
     *
     * @param shellExecutable 用于执行命令的 Shell 可执行文件，例如 `sh` 或 `su`。
     * @param pluginEntryPoint 插件的可执行入口点。
     * @param pluginPackageDirectory 插件所在目录，同时作为执行时的工作目录。
     * @return 可直接传递给 [ProcessBuilder] 的命令参数列表。
     *
     * @since 2026-10-03
     * @lastModified 2026-10-03
     */
    fun commandFactory(
        shellExecutable: String,
        pluginEntryPoint: String,
        pluginPackageDirectory: String,
    ): List<String> {

        val commandList = mutableListOf<String>()

        commandList += shellExecutable
        commandList += "-c"
        commandList += listOf(
            "cd $pluginPackageDirectory",
            "echo PID:$$",
            "exec $pluginEntryPoint"
        ).joinToString(" ; ")

        return commandList

    }

}
