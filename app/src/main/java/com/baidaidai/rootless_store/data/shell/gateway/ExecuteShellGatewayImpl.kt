package com.baidaidai.rootless_store.data.shell.gateway

import android.util.Log
import com.baidaidai.rootless_store.data.fileSystem.gateway.AndroidFileSystemCapabilityGatewayImpl
import com.baidaidai.rootless_store.data.shell.provider.ShellExecutionContextProviderImpl
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import com.baidaidai.rootless_store.data.shizuku.server.ShizukuEndpointCallback
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResultTag
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
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl,
    private val androidFileSystemCapabilityGatewayImpl: AndroidFileSystemCapabilityGatewayImpl,
    private val shellExecutionContextProviderImpl: ShellExecutionContextProviderImpl
) {

    private var currentDirectory: String = "/sdcard"

    fun executeCommandByAppShell(commandContent: String): Flow<ShellResult> = callbackFlow {
        var commandContent = commandContent

        val appShellContextConfig = shellExecutionContextProviderImpl.resolveAppShellContext()

        if(appShellContextConfig.shouldJumpToDirectory){
            prepareWorkingDirectoryCommand(androidFileSystemCapabilityGatewayImpl.getDefaultPluginDirectoryPath())
        }
        if(commandContent.startsWith("cd ")){
            val targetDirectory = commandContent.removePrefix("cd ").trim()
            prepareWorkingDirectoryCommand(targetDirectory)
            commandContent = "exit"
        }

        val appShellProcessBuilder = ProcessBuilder("sh","-c", "${prepareWorkingDirectoryCommand()}$commandContent")

        val environment = appShellProcessBuilder.environment()
        val oldPath = environment["PATH"].orEmpty()
        val oldLdPath = environment["LD_LIBRARY_PATH"].orEmpty()

        environment["PATH"] = "${appShellContextConfig.environmentPath}:$oldPath"
        environment["LD_LIBRARY_PATH"] = "${appShellContextConfig.environmentLdPath}:$oldLdPath"
        environment.putAll(appShellContextConfig.environmentConfig)

        Log.d("executePluginEntryPoint","environmentPath: ${appShellContextConfig.environmentPath}")
        Log.d("executePluginEntryPoint","environmentLdPath: ${appShellContextConfig.environmentLdPath}")

        val appShellProcess = appShellProcessBuilder.start()

        launch(Dispatchers.IO) {
            appShellProcess.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ShellResult(
                            resultTag = ExecutionResultTag.Normal,
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
                            resultTag = ExecutionResultTag.RedLine,
                            command = "~ $commandContent",
                            content = error,
                        )
                    )
                }
            }
        }

        awaitClose {}

    }.flowOn(Dispatchers.IO)

    fun executeCommandByAdbShell(commandContent: String): Flow<ShellResult> = callbackFlow {

        val adbShellContextConfig = shellExecutionContextProviderImpl.resolveAdbShellContext()

        launch(Dispatchers.IO) {
            val callback = ShizukuEndpointCallback(
                onExecuteCallback = { session ->
                    trySend(
                        ShellResult(
                            resultTag = ExecutionResultTag.Normal,
                            command = "~ $commandContent",
                            content = session.toString(),
                        )
                    )
                },
                onErrorCallback = { error ->
                    trySend(
                        ShellResult(
                            resultTag = ExecutionResultTag.RedLine,
                            command = "~ $commandContent",
                            content = error.toString(),
                        )
                    )
                },
                onProcessExitedCallback = {}
            )

            Log.d("exam",(shizukuUserServiceGatewayImpl.getShizukuUserService()==null).toString())

            shizukuUserServiceGatewayImpl.getShizukuUserService()
                ?.command(
                    commandContent,
                    callback,
                    adbShellContextConfig.shouldJumpToDirectory
                )
        }
        awaitClose {  }
    }

    fun executeCommandByRootShell(commandContent: String): Flow<ShellResult> = callbackFlow {

        var commandContent = commandContent

        if(commandContent.startsWith("cd ")){
            val targetDirectory = commandContent.removePrefix("cd ").trim()
            prepareWorkingDirectoryCommand(targetDirectory)
            commandContent = "exit"
        }

        val process = ProcessBuilder("su", "-c", "${prepareWorkingDirectoryCommand()}$commandContent").start()

        launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { result ->
                    send(
                        ShellResult(
                            resultTag = ExecutionResultTag.Normal,
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
                            resultTag = ExecutionResultTag.RedLine,
                            command = "# $commandContent",
                            content = error,
                        )
                    )
                }
            }
        }

        awaitClose {}

    }.flowOn(Dispatchers.IO)

    private fun prepareWorkingDirectoryCommand(directory: String = currentDirectory): String{

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
