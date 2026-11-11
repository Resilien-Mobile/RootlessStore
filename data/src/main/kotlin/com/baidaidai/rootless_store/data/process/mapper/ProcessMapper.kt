package com.baidaidai.rootless_store.data.process.mapper

import com.baidaidai.rootless_store.data.process.shizuku.dto.ProcCmdlineDto
import com.baidaidai.rootless_store.data.process.shizuku.dto.ProcessTypeDto
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.baidaidai.rootless_store.domain.process.model.ProcessType

object ProcessMapper {

    fun ProcCmdlineDto.toProcessInfo(): ProcessInfo {

        val processType = when (processType) {

            ProcessTypeDto.LinuxProcess -> ProcessType.LinuxProcess
            ProcessTypeDto.AndroidPackage -> ProcessType.AndroidPackage
            ProcessTypeDto.Unknown -> ProcessType.Unknown

        }

        return ProcessInfo(
            pid = pid,
            processName = processName,
            packageName = packageName,
            processType = processType
        )

    }

}
