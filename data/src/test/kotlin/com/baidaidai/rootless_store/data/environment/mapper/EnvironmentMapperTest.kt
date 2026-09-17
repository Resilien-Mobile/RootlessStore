package com.baidaidai.rootless_store.data.environment.mapper

import com.baidaidai.rootless_store.data.environment.database.EnvironmentEntity
import com.baidaidai.rootless_store.data.environment.database.EnvironmentStatusEntity
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentEntity
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentManifest
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentStatus
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentStatusEntity
import com.baidaidai.rootless_store.domain.environment.manifest.EnvironmentManifest
import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import org.junit.Assert.assertEquals
import org.junit.Test

class EnvironmentMapperTest {

    @Test
    fun environmentManifestToEnvironmentEntityTest() {
        val fakeEnvironmentManifest = EnvironmentManifest(
            installedVersion = "1.0.0",
            environmentRenderingName = "BusyBox",
            environmentPackageName = "com.example.busybox",
            environmentId = "environment-busybox",
            iconUri = "file://icons/busybox.png",
            author = "Rootless Store",
            environmentDescription = "Provides command line tools.",
            requiredEnvironment = ExecutionContext.PERMISSIVE,
            entryPoint = "bin/sh",
            ldLibraryPath = listOf("lib", "usr/lib"),
            env = mapOf("PATH" to "bin", "TMPDIR" to "tmp"),
            environmentUrl = "https://example.com/environment.zip"
        )

        val environmentEntity = fakeEnvironmentManifest.toEnvironmentEntity()

        val expectedEnvironmentEntity = EnvironmentEntity(
            environmentId = "environment-busybox",
            installedVersion = "1.0.0",
            environmentRenderingName = "BusyBox",
            environmentPackageName = "com.example.busybox",
            iconUri = "file://icons/busybox.png",
            author = "Rootless Store",
            environmentDescription = "Provides command line tools.",
            requiredEnvironment = ExecutionContext.PERMISSIVE,
            entryPoint = "bin/sh",
            ldLibraryPath = listOf("lib", "usr/lib"),
            env = mapOf("PATH" to "bin", "TMPDIR" to "tmp")
        )
        assertEquals(expectedEnvironmentEntity, environmentEntity)
    }

    @Test
    fun environmentEntityToEnvironmentManifestTest() {
        val fakeEnvironmentEntity = EnvironmentEntity(
            environmentId = "environment-busybox",
            installedVersion = "1.0.0",
            environmentRenderingName = "BusyBox",
            environmentPackageName = "com.example.busybox",
            iconUri = "file://icons/busybox.png",
            author = "Rootless Store",
            environmentDescription = "Provides command line tools.",
            requiredEnvironment = ExecutionContext.PERMISSIVE,
            entryPoint = "bin/sh",
            ldLibraryPath = listOf("lib", "usr/lib"),
            env = mapOf("PATH" to "bin", "TMPDIR" to "tmp")
        )

        val environmentManifest = fakeEnvironmentEntity.toEnvironmentManifest()

        val expectedEnvironmentManifest = EnvironmentManifest(
            installedVersion = "1.0.0",
            environmentRenderingName = "BusyBox",
            environmentPackageName = "com.example.busybox",
            environmentId = "environment-busybox",
            iconUri = "file://icons/busybox.png",
            author = "Rootless Store",
            environmentDescription = "Provides command line tools.",
            requiredEnvironment = ExecutionContext.PERMISSIVE,
            entryPoint = "bin/sh",
            ldLibraryPath = listOf("lib", "usr/lib"),
            env = mapOf("PATH" to "bin", "TMPDIR" to "tmp")
        )
        assertEquals(expectedEnvironmentManifest, environmentManifest)
    }

    @Test
    fun environmentStatusEntityToEnvironmentStatusTest() {
        val fakeEnvironmentStatusEntity = EnvironmentStatusEntity(
            environmentId = "environment-busybox",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Official
        )

        val environmentStatus = fakeEnvironmentStatusEntity.toEnvironmentStatus()

        val expectedEnvironmentStatus = EnvironmentStatus(
            environmentId = "environment-busybox",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Official
        )
        assertEquals(expectedEnvironmentStatus, environmentStatus)
    }

    @Test
    fun environmentStatusToEnvironmentStatusEntityTest() {
        val fakeEnvironmentStatus = EnvironmentStatus(
            environmentId = "environment-busybox",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Official
        )

        val environmentStatusEntity = fakeEnvironmentStatus.toEnvironmentStatusEntity()

        val expectedEnvironmentStatusEntity = EnvironmentStatusEntity(
            environmentId = "environment-busybox",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Official
        )
        assertEquals(expectedEnvironmentStatusEntity, environmentStatusEntity)
    }
}
