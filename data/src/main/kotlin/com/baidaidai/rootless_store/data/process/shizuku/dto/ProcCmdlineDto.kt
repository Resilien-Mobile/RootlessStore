package com.baidaidai.rootless_store.data.process.shizuku.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcCmdlineDto(
    val pid: Int,
    val processName: String?,
    val packageName: String?,
    val processType: ProcessTypeDto
) : Parcelable
