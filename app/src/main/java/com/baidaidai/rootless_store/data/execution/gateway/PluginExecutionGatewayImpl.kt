package com.baidaidai.rootless_store.data.execution.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.monitor.ProcessMonitor
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
    private val processMonitor: ProcessMonitor
) {

    internal fun resolveLocalShellExecutable(): String{
        val shell = Shell.getShell()
        return if (shell.isRoot){
            "su"
        }else{
            "sh"
        }
    }
    fun executePluginEntryPoint(
        pluginEntryPoint: String,
        pluginPackageDirectory: String,
        shouldMonitor: Boolean = false
    ): Flow<ExecutionResult> = callbackFlow {
        val processBuilder = ProcessBuilder(
            resolveLocalShellExecutable(), "-c", "cd $pluginPackageDirectory ;echo PID:$$;exec $pluginEntryPoint"
        )

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

        val process = processBuilder.start()

        if (shouldMonitor){
            processMonitor(process)
        }

        launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Normal,
                            content = "- ${result.toString()}"
                        )
                    )
                }
            }
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { error ->
                    send(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Normal,
                            content = "- ${error.toString()}"
                        )
                    )
                }
            }
        }

        awaitClose {
        }
    }
        .flowOn(Dispatchers.IO)

    fun executePluginWithoutEnvironmentByShizuku(
        pluginDirectory: String,
        pluginEntryPoint: String,
        shouldMonitor: Boolean
    ): Flow<ExecutionResult> = callbackFlow {
        launch(Dispatchers.IO) {
            val callback = ShizukuEndpointCallback(
                onExecuteCallback = { session ->
                    trySend(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.Normal,
                            content = "- ${session.toString()}"
                        )
                    )
                },
                onErrorCallback = { error ->
                    trySend(
                        ExecutionResult(
                            resultTag = ExecutionResultTag.RedLine,
                            content = "- ${error.toString()}"
                        )
                    )
                },
                onProcessExitedCallback = { exitCode ->
                    processMonitor(exitCode)
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

            val result = shizukuUserServiceGatewayImpl.findShizukuUserService()
                ?.kill(pluginProcessPid)

            Log.d("kill pid result",result.toString())

            result != null
        }else{
            false
        }
    }

}
