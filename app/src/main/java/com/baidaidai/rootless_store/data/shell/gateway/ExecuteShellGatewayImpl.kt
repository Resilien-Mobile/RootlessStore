package com.baidaidai.rootless_store.data.shell.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.shell.provider.ShellExecuteContextProviderImpl
import com.baidaidai.rootless_store.data.shizuku.repository.ShizukuAdbRepositoryImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.execute.model.ResultTag
import com.baidaidai.rootless_store.domain.shell.model.ShellResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.text.orEmpty

class ExecuteShellGatewayImpl @Inject constructor(
    private val shizukuAdbRepositoryImpl: ShizukuAdbRepositoryImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val shellExecuteContextProviderImpl: ShellExecuteContextProviderImpl
) {

    private var currentDirectory: String = "/sdcard"

    fun runCommandByAppShell(commandContent: String): Flow<ShellResult> = callbackFlow {
        var commandContent = commandContent

        val appShellContextConfig = shellExecuteContextProviderImpl.getAppShellContext()

        if(appShellContextConfig.jumpToDirectory){
            changeDirectoryHandler(androidFileSystemCapabilityGatewayImpl.getDefaultPluginDirectoryPath())
        }
        if(commandContent.startsWith("cd ")){
            val targetDirectory = commandContent.removePrefix("cd ").trim()
            changeDirectoryHandler(targetDirectory)
            commandContent = "exit"
        }

        val appShellProcessBuilder = ProcessBuilder("sh","-c", "${changeDirectoryHandler()}$commandContent")

        val environment = appShellProcessBuilder.environment()
        val oldPATH = environment["PATH"].orEmpty()
        val oldLDPATH = environment["LD_LIBRARY_PATH"].orEmpty()

        environment["PATH"] = "${appShellContextConfig.environmentPATH}:$oldPATH"
        environment["LD_LIBRARY_PATH"] = "${appShellContextConfig.environmentLDPATH}:$oldLDPATH"
        environment.putAll(appShellContextConfig.environmentConfig)

        Log.d("executePluginEntryPoint","environmentPATH: ${appShellContextConfig.environmentPATH}")
        Log.d("executePluginEntryPoint","environmentLDPATH: ${appShellContextConfig.environmentLDPATH}")

        val appShellProcess = appShellProcessBuilder.start()

        launch(Dispatchers.IO) {
            appShellProcess.inputStream.bufferedReader().useLines { lines ->
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
        }
        launch(Dispatchers.IO){
            appShellProcess.errorStream.bufferedReader().useLines { lines ->
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

        val adbShellContextConfig = shellExecuteContextProviderImpl.getAdbShellContext()

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
                },
                onProcessExitedCallback = {}
            )

            Log.d("exam",(shizukuAdbRepositoryImpl.getShizukuEndpoint()==null).toString())

            shizukuAdbRepositoryImpl.getShizukuEndpoint()
                ?.command(
                    commandContent,
                    adbShellContextConfig.environmentPATH,
                    adbShellContextConfig.environmentLDPATH,
                    adbShellContextConfig.environmentConfigKeyList,
                    adbShellContextConfig.environmentConfigValueList,
                    callback,
                    adbShellContextConfig.useRunAs
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

    private fun changeDirectoryHandler(directory: String = currentDirectory): String{

        currentDirectory = when {
            directory.startsWith("/") -> {
                File(directory).canonicalPath
            }
            else -> {
                File(currentDirectory, directory).canonicalPath
            }
        }

        return "cd $currentDirectory &&"

    }

}