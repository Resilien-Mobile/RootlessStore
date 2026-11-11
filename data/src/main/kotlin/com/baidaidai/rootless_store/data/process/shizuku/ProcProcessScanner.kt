package com.baidaidai.rootless_store.data.process.shizuku

import android.content.Context
import androidx.annotation.Keep
import IProcProcessScanner
import IProcProcessScannerCallback
import com.baidaidai.rootless_store.data.process.shizuku.dto.ProcCmdlineDto
import com.baidaidai.rootless_store.data.process.shizuku.dto.ProcessTypeDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class ProcProcessScanner @Keep constructor(
    private val context: Context
) : IProcProcessScanner.Stub() {

    // States
    @Volatile
    private var procCmdlineByPid: Map<Int, String> = emptyMap()
    private var installedPackageNameCache: Set<String>? = null

    // Values
    private val packageManager = context.packageManager
    private val coroutineScope = CoroutineScope(
        context = SupervisorJob() + Dispatchers.IO
    )


    override fun scan(callback: IProcProcessScannerCallback) {

        coroutineScope.launch {

            // 先更新 /proc 缓存，再解析进程类型。
            updateProcCmdlineCache()
            callback.onResult(resolveProcessList())

        }

    }

    internal val processList: List<ProcCmdlineDto>
        get() = procCmdlineByPid
            .toSortedMap()
            .map { (processId, cmdline) ->

                if (cmdline.startsWith(prefix = "/")) {

                    ProcCmdlineDto(
                        pid = processId,
                        processName = cmdline,
                        packageName = null,
                        processType = ProcessTypeDto.LinuxProcess
                    )

                } else {

                    ProcCmdlineDto(
                        pid = processId,
                        processName = cmdline,
                        packageName = null,
                        processType = ProcessTypeDto.Unknown
                    )

                }

            }

    internal fun updateProcCmdlineCache() {

        // 每轮扫描构建完整快照，避免已退出进程继续留在上轮缓存中。
        val currentProcCmdlineByPid = HashMap<Int, String>()
        val processDirectories = File("/proc").listFiles().orEmpty()

        processDirectories.forEach { processDirectory ->

            val processId = processDirectory.name.toIntOrNull() ?: return@forEach
            val cmdlineFile = File(processDirectory, "cmdline")

            val cmdline = runCatching {
                cmdlineFile.readText()
                    .trimEnd(chars = charArrayOf('\u0000'))
                    .replace(oldChar = '\u0000', newChar = ' ')
            }.getOrNull() ?: return@forEach

            currentProcCmdlineByPid[processId] = cmdline
        }

        procCmdlineByPid = currentProcCmdlineByPid

    }

    internal fun listInstalledPackageNames(): List<String> =
        packageManager
            .getInstalledApplications(0)
            .map{ applicationInfo ->

                applicationInfo.packageName

            }

    internal fun resolveProcessList(): List<ProcCmdlineDto> {

        val installedPackageNameSet = installedPackageNameCache
            ?: listInstalledPackageNames()
                .toHashSet()
                .also { installedPackageNameCache = it }

        // 将非 Linux 命令行与已安装包名匹配，生成最终进程分类。
        return processList.map { processInfo ->

            if (processInfo.processType == ProcessTypeDto.LinuxProcess) {

                processInfo

            } else {

                val processName = processInfo.processName
                val packageName = processName?.substringBefore(delimiter = ':')

                if (packageName != null && packageName in installedPackageNameSet) {

                    processInfo.copy(
                        processName = null,
                        packageName = packageName,
                        processType = ProcessTypeDto.AndroidPackage
                    )

                } else {

                    processInfo.copy(
                        processType = ProcessTypeDto.Unknown
                    )

                }

            }

        }

    }

}
