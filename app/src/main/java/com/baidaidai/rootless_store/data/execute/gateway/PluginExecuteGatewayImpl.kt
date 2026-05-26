package com.baidaidai.rootless_store.data.execute.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.monitor.ProcessMonitor
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.repository.ShizukuAdbRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.execute.model.ResultTag
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class PluginExecuteGatewayImpl @Inject constructor(
    private val shizukuAdbRepositoryImpl: ShizukuAdbRepositoryImpl,
    private val pluginCoreRepositoryImpl: PluginCoreRepositoryImpl,
    private val processMonitor: ProcessMonitor
) {

    internal fun createCallbackList(
        onAddingElement: (String) -> Unit
    ): CallbackList<String> {
        return object : CallbackList<String>() {
            override fun onAddElement(s: String) = onAddingElement(s)
        }
    }

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

        val environmentPATH = pluginCoreRepositoryImpl.getAvailableEnvironmentPath()
        val environmentLDPATH = pluginCoreRepositoryImpl.getAvailableEnvironmentLDPATH()
        val environmentConfig = pluginCoreRepositoryImpl.getAvailableEnvironmentConfig()

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
    ): Flow<ExecuteResult> =

        callbackFlow {
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

                Log.d("exam",(shizukuAdbRepositoryImpl.getShizukuEndpoint()==null).toString())

                val environmentPATH = pluginCoreRepositoryImpl.getAvailableEnvironmentPath()
                val environmentLDPATH = pluginCoreRepositoryImpl.getAvailableEnvironmentLDPATH()
                val environmentConfigKeyList = pluginCoreRepositoryImpl.getEnvironmentConfigKeyList()
                val environmentConfigValueList = pluginCoreRepositoryImpl.getEnvironmentConfigValueList()

                shizukuAdbRepositoryImpl.getShizukuEndpoint()
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

    fun abortPluginProcess(pluginProcessPID: Int?){
        if (pluginProcessPID != null){
            ProcessBuilder(
                rootEnvironmentSwitch(), "-c", "kill $pluginProcessPID"
            ).start()
        }
    }

    fun abortPluginProcessByShizuku(pluginProcessPID: Int?){
        if (pluginProcessPID != null){
            Log.d("exam","shizuku ${shizukuAdbRepositoryImpl.getShizukuEndpoint() == null}")
            Log.d("pid","$pluginProcessPID")

            val result = shizukuAdbRepositoryImpl.getShizukuEndpoint()
                ?.kill(pluginProcessPID)

            Log.d("kill pid result",result.toString())
        }
    }
}
