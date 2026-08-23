package com.baidaidai.rootless_store.domain.status.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class CpuDashboardConfig(
    val coreCount: Int,
    val aggregateMetrics: CpuCoreMetrics,
    val coreMetrics: List<CpuCoreMetrics>,
    val uptime: Duration,
){
    companion object {
        val _testOnly_ = CpuDashboardConfig(
            coreCount = 8,
            coreMetrics = listOf(
                CpuCoreMetrics(
                    userDelta = 42,
                    systemDelta = 15,
                    idleDelta = 36,
                    ioWaitDelta = 5,
                    stealDelta = 2,
                    totalPercent = 0.3f
                ),
                CpuCoreMetrics(
                    userDelta = 20,
                    systemDelta = 12,
                    idleDelta = 61,
                    ioWaitDelta = 6,
                    stealDelta = 1,
                    totalPercent = 0.2f
                ),
                CpuCoreMetrics(
                    userDelta = 55,
                    systemDelta = 18,
                    idleDelta = 21,
                    ioWaitDelta = 4,
                    stealDelta = 2,
                    totalPercent = 0.1f
                ),
                CpuCoreMetrics(
                    userDelta = 8,
                    systemDelta = 7,
                    idleDelta = 81,
                    ioWaitDelta = 3,
                    stealDelta = 1,
                    totalPercent = 0.7f
                ),
                CpuCoreMetrics(
                    userDelta = 31,
                    systemDelta = 11,
                    idleDelta = 52,
                    ioWaitDelta = 5,
                    stealDelta = 1,
                    totalPercent = 0.48f
                ),
                CpuCoreMetrics(
                    userDelta = 12,
                    systemDelta = 8,
                    idleDelta = 75,
                    ioWaitDelta = 4,
                    stealDelta = 1,
                    totalPercent = 0.25f
                ),
                CpuCoreMetrics(
                    userDelta = 48,
                    systemDelta = 20,
                    idleDelta = 25,
                    ioWaitDelta = 5,
                    stealDelta = 2,
                    totalPercent = 0.75f
                ),
                CpuCoreMetrics(
                    userDelta = 6,
                    systemDelta = 5,
                    idleDelta = 85,
                    ioWaitDelta = 3,
                    stealDelta = 1,
                    totalPercent = 0.15f
                )
            ),
            aggregateMetrics = CpuCoreMetrics(
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

data class CpuCoreMetrics(

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
