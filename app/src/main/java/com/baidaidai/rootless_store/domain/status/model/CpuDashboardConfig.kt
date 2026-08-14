package com.baidaidai.rootless_store.domain.status.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class CpuDashboardConfig(
    val coreCount: Int,
    val totalCoreInfo: CoreInfo,
    val core: List<CoreInfo>,
    val uptime: Duration,
){
    companion object {
        val _testOnly_ = CpuDashboardConfig(
            coreCount = 8,
            core = listOf(
                CoreInfo(
                    userDelta = 42,
                    systemDelta = 15,
                    idleDelta = 36,
                    ioWaitDelta = 5,
                    stealDelta = 2,
                    totalPercent = 0.3f
                ),
                CoreInfo(
                    userDelta = 20,
                    systemDelta = 12,
                    idleDelta = 61,
                    ioWaitDelta = 6,
                    stealDelta = 1,
                    totalPercent = 0.2f
                ),
                CoreInfo(
                    userDelta = 55,
                    systemDelta = 18,
                    idleDelta = 21,
                    ioWaitDelta = 4,
                    stealDelta = 2,
                    totalPercent = 0.1f
                ),
                CoreInfo(
                    userDelta = 8,
                    systemDelta = 7,
                    idleDelta = 81,
                    ioWaitDelta = 3,
                    stealDelta = 1,
                    totalPercent = 0.7f
                ),
                CoreInfo(
                    userDelta = 31,
                    systemDelta = 11,
                    idleDelta = 52,
                    ioWaitDelta = 5,
                    stealDelta = 1,
                    totalPercent = 0.48f
                ),
                CoreInfo(
                    userDelta = 12,
                    systemDelta = 8,
                    idleDelta = 75,
                    ioWaitDelta = 4,
                    stealDelta = 1,
                    totalPercent = 0.25f
                ),
                CoreInfo(
                    userDelta = 48,
                    systemDelta = 20,
                    idleDelta = 25,
                    ioWaitDelta = 5,
                    stealDelta = 2,
                    totalPercent = 0.75f
                ),
                CoreInfo(
                    userDelta = 6,
                    systemDelta = 5,
                    idleDelta = 85,
                    ioWaitDelta = 3,
                    stealDelta = 1,
                    totalPercent = 0.15f
                )
            ),
            totalCoreInfo = CoreInfo(
                userDelta = 31,
                systemDelta = 13,
                idleDelta = 50,
                ioWaitDelta = 5,
                stealDelta = 1,
                totalPercent = 0.346f
            ),
            uptime = 12.hours
        )
    }
}

data class CoreInfo(

    /**
     * 用户态delta
     *
     * USER
     */
    val userDelta: Int,

    /**
     * 内核态delta
     *
     * SYS
     */
    val systemDelta: Int,

    /**
     * 空闲
     *
     * IDLE delta
     */
    val idleDelta: Int,

    /**
     * IO等待
     *
     * IO WAIT delta
     */
    val ioWaitDelta: Int,

    /**
     * 虚拟化偷取delta
     *
     * STEAL
     */
    val stealDelta: Int,

    /**
     * 总占用率
     *
     * 方便UI使用
     *
     * = 100 - idle
     */
    val totalPercent: Float
)
