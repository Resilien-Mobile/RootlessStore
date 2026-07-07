package com.baidaidai.rootless_store.data.execute.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.environment.repository.EnvironmentRepositoryImpl
import com.baidaidai.rootless_store.data.monitor.ProcessMonitor
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.execute.model.ResultTag
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class PluginExecuteGatewayImpl @Inject constructor(
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val environmentRepositoryImpl: EnvironmentRepositoryImpl,
    private val processMonitor: ProcessMonitor
) {

    internal fun rootEnvironmentSwitch(): String{
        val shell = Shell.getShell()
        return if (shell.isRoot){
            "su"
        }else{
            "sh"
        }
    }
    fun executePluginEntryPoint(
        pluginExecuteEntryPoint: String,
        pluginPackageDirectory: String,
        enableMonitor: Boolean = false
    ): Flow<ExecuteResult> = callbackFlow {
        val processBuilder = ProcessBuilder(
            rootEnvironmentSwitch(), "-c", "cd $pluginPackageDirectory ;echo PID:$$;exec $pluginExecuteEntryPoint"
        )

        val environment = processBuilder.environment()

        val oldPATH = environment["PATH"].orEmpty()
        val oldLDPATH = environment["LD_LIBRARY_PATH"].orEmpty()

        val environmentPATH = environmentRepositoryImpl.getAvailableEnvironmentPath()
        val environmentLDPATH = environmentRepositoryImpl.getAvailableEnvironmentLDPATH()
        val environmentConfig = environmentRepositoryImpl.getAvailableEnvironmentConfig()

        environment["PATH"] = "$environmentPATH:$oldPATH"
        environment["LD_LIBRARY_PATH"] = "$environmentLDPATH:$oldLDPATH"
        environment.putAll(environmentConfig)

        Log.d("executePluginEntryPoint","environmentPATH: $environmentPATH")
        Log.d("executePluginEntryPoint","environmentLDPATH: $environmentLDPATH")

        val process = processBuilder.start()

        if (enableMonitor){
            processMonitor(process)
        }

        launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ExecuteResult(
                            resulTag = ResultTag.Normal,
                            content = "- ${result.toString()}"
                        )
                    )
                }
            }
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { error ->
                    send(
                        ExecuteResult(
                            resulTag = ResultTag.Normal,
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

    fun executePluginEntryPointByShizuku(
        pluginExecuteEntryPoint: String,
        pluginPackageDirectory: String,
        enableMonitor: Boolean
    ): Flow<ExecuteResult> = callbackFlow {
            launch(Dispatchers.IO) {
                val callback = ShizukuEndpointCallback(
                    onExecuteCallback = { session ->
                        trySend(
                            ExecuteResult(
                                resulTag = ResultTag.Normal,
                                content = "- ${session.toString()}"
                            )
                        )
                    },
                    onErrorCallback = { error ->
                        trySend(
                            ExecuteResult(
                                resulTag = ResultTag.RedLine,
                                content = "- ${error.toString()}"
                            )
                        )
                    },
                    onProcessExitedCallback = { exitCode ->
                        processMonitor(exitCode)
                    }
                )

                Log.d("exam",(shizukuUserServiceGatewayImpl.getShizukuUserService() != null).toString())

                val environmentPATH = environmentRepositoryImpl.getAvailableEnvironmentPath()
                val environmentLDPATH = environmentRepositoryImpl.getAvailableEnvironmentLDPATH()
                val environmentConfigKeyList = environmentRepositoryImpl.getEnvironmentConfigKeyList()
                val environmentConfigValueList = environmentRepositoryImpl.getEnvironmentConfigValueList()

                shizukuUserServiceGatewayImpl.getShizukuUserService()
                    ?.exec(
                        pluginExecuteEntryPoint,
                        pluginPackageDirectory,
                        callback,
                        environmentPATH,
                        environmentLDPATH,
                        environmentConfigKeyList,
                        environmentConfigValueList,
                        enableMonitor
                    )
            }
            awaitClose {  }
        }

    fun executePluginWithoutEnvironmentByShizuku(
        pluginContent: String,
        enableMonitor: Boolean
    ): Flow<ExecuteResult> = callbackFlow {
        launch(Dispatchers.IO) {
            val callback = ShizukuEndpointCallback(
                onExecuteCallback = { session ->
                    trySend(
                        ExecuteResult(
                            resulTag = ResultTag.Normal,
                            content = "- ${session.toString()}"
                        )
                    )
                },
                onErrorCallback = { error ->
                    trySend(
                        ExecuteResult(
                            resulTag = ResultTag.RedLine,
                            content = "- ${error.toString()}"
                        )
                    )
                },
                onProcessExitedCallback = { exitCode ->
                    processMonitor(exitCode)
                }
            )

            shizukuUserServiceGatewayImpl.getShizukuUserService()
                ?.execWithoutEnvironment(pluginContent, enableMonitor, callback)
        }
        awaitClose {  }
    }

    fun abortPluginProcess(pluginProcessPID: Int?){
        if (pluginProcessPID != null){
            ProcessBuilder(
                rootEnvironmentSwitch(), "-c", "kill -9 $pluginProcessPID"
            ).start()
        }
    }

    fun abortPluginProcessByShizuku(pluginProcessPID: Int?): Boolean{
        return if (pluginProcessPID != null){
            Log.d("exam","shizuku ${shizukuUserServiceGatewayImpl.getShizukuUserService() == null}")
            Log.d("pid","$pluginProcessPID")

            val result = shizukuUserServiceGatewayImpl.getShizukuUserService()
                ?.kill(pluginProcessPID)

            Log.d("kill pid result",result.toString())

            result != null
        }else{
            false
        }
    }

}
