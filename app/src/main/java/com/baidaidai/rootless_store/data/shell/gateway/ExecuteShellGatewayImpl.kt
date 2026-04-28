package com.baidaidai.rootless_store.data.shell.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.plugin.repository.PluginCoreRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.repository.ShizukuAdbRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.execute.model.ExecuteResult
import com.baidaidai.rootless_store.domain.execute.model.ResultTag
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.orEmpty

class ExecuteShellGatewayImpl @Inject constructor(
    private val shizukuAdbRepositoryImpl: ShizukuAdbRepositoryImpl,
    private val pluginCoreRepositoryImpl: PluginCoreRepositoryImpl
) {

    fun runCommandByAppShell(commandContent: String): Flow<ShellResult> = callbackFlow {
        val processBuilder = ProcessBuilder("sh", "-c", commandContent)

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

        launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ShellResult(
                            resulTag = ResultTag.Normal,
                            command = "~ $commandContent",
                            content = result,
                        )
                    )
                }
            }
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { error ->
                    send(
                        ShellResult(
                            resulTag = ResultTag.RedLine,
                            command = "~ $commandContent",
                            content = error,
                        )
                    )
                }
            }
        }

        awaitClose {}

    }.flowOn(Dispatchers.IO)

    fun runCommandByADBShell(commandContent: String): Flow<ShellResult> = callbackFlow {
        launch(Dispatchers.IO) {
            val callback = ShizukuEndpointCallback(
                onExecuteCallback = { session ->
                    trySend(
                        ShellResult(
                            resulTag = ResultTag.Normal,
                            command = "~ $commandContent",
                            content = session.toString(),
                        )
                    )
                },
                onErrorCallback = { error ->
                    trySend(
                        ShellResult(
                            resulTag = ResultTag.RedLine,
                            command = "~ $commandContent",
                            content = error.toString(),
                        )
                    )
                }
            )

            Log.d("exam",(shizukuAdbRepositoryImpl.getShizukuEndpoint()==null).toString())

            val environmentPATH = pluginCoreRepositoryImpl.getAvailableEnvironmentPath()
            val environmentLDPATH = pluginCoreRepositoryImpl.getAvailableEnvironmentLDPATH()
            val environmentConfigKeyList = pluginCoreRepositoryImpl.getEnvironmentConfigKeyList()
            val environmentConfigValueList = pluginCoreRepositoryImpl.getEnvironmentConfigValueList()

            shizukuAdbRepositoryImpl.getShizukuEndpoint()
                ?.command(
                    commandContent,
                    environmentPATH,
                    environmentLDPATH,
                    environmentConfigKeyList,
                    environmentConfigValueList,
                    callback
                )
        }
        awaitClose {  }
    }

    fun runCommandByRootShell(commandContent: String): Flow<ShellResult> = callbackFlow {
        val process = ProcessBuilder("su", "-c", commandContent).start()

        launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ShellResult(
                            resulTag = ResultTag.Normal,
                            command = "# $commandContent",
                            content = result,
                        )
                    )
                }
            }
            process.errorStream.bufferedReader().useLines { lines ->
                lines.forEach { error ->
                    send(
                        ShellResult(
                            resulTag = ResultTag.RedLine,
                            command = "# $commandContent",
                            content = error,
                        )
                    )
                }
            }
        }

        awaitClose {}

    }.flowOn(Dispatchers.IO)

}