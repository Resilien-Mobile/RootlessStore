package com.baidaidai.rootless_store.data.plugin.mapper

import com.baidaidai.rootless_store.data.plugin.database.PluginEntity
import com.baidaidai.rootless_store.data.plugin.database.PluginStatusEntity
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginEntity
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginManifest
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginStatus
import com.baidaidai.rootless_store.data.plugin.mapper.PluginMapper.toPluginStatusEntity
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import com.baidaidai.rootless_store.domain.plugin.model.PluginStatus
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import org.junit.Assert.assertEquals
import org.junit.Test

class PluginMapperTest {

    @Test
    fun pluginManifestToPluginEntityTest() {
        val fakePluginManifest = PluginManifest(
            installedVersion = "1.2.3",
            pluginRenderingName = "Power Menu",
            pluginPackageName = "com.example.power.menu",
            pluginId = "plugin-power-menu",
            iconUri = "file://icons/power.png",
            author = "Rootless Store",
            pluginDescription = "Adds power actions.",
            requiredEnvironment = ExecutionContext.ADB,
            pluginRunModel = PluginRunModel.OneTime,
            entryPoint = "index.sh",
            webUiEntryPoint = "webroot/index.html",
            executableFiles = listOf("index.sh"),
            pluginUrl = "https://example.com/plugin.zip"
        )

        val pluginEntity = fakePluginManifest.toPluginEntity()

        val expectedPluginEntity = PluginEntity(
            pluginId = "plugin-power-menu",
            installedVersion = "1.2.3",
            pluginRenderingName = "Power Menu",
            pluginPackageName = "com.example.power.menu",
            iconUri = "file://icons/power.png",
            author = "Rootless Store",
            pluginDescription = "Adds power actions.",
            requiredEnvironment = ExecutionContext.ADB,
            entryPoint = "index.sh",
            pluginRunModel = PluginRunModel.OneTime,
            webUiEntryPoint = "webroot/index.html"
        )
        assertEquals(expectedPluginEntity, pluginEntity)
    }

    @Test
    fun pluginEntityToPluginManifestTest() {
        val fakePluginEntity = PluginEntity(
            pluginId = "plugin-power-menu",
            installedVersion = "1.2.3",
            pluginRenderingName = "Power Menu",
            pluginPackageName = "com.example.power.menu",
            iconUri = "file://icons/power.png",
            author = "Rootless Store",
            pluginDescription = "Adds power actions.",
            requiredEnvironment = ExecutionContext.ADB,
            entryPoint = "index.sh",
            pluginRunModel = PluginRunModel.OneTime,
            webUiEntryPoint = "webroot/index.html"
        )

        val pluginManifest = fakePluginEntity.toPluginManifest()

        val expectedPluginManifest = PluginManifest(
            installedVersion = "1.2.3",
            pluginRenderingName = "Power Menu",
            pluginPackageName = "com.example.power.menu",
            pluginId = "plugin-power-menu",
            iconUri = "file://icons/power.png",
            author = "Rootless Store",
            pluginDescription = "Adds power actions.",
            requiredEnvironment = ExecutionContext.ADB,
            pluginRunModel = PluginRunModel.OneTime,
            entryPoint = "index.sh",
            webUiEntryPoint = "webroot/index.html"
        )
        assertEquals(expectedPluginManifest, pluginManifest)
    }

    @Test
    fun pluginStatusEntityToPluginStatusTest() {
        val fakePluginStatusEntity = PluginStatusEntity(
            pluginId = "plugin-power-menu",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Local
        )

        val pluginStatus = fakePluginStatusEntity.toPluginStatus()

        val expectedPluginStatus = PluginStatus(
            pluginId = "plugin-power-menu",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Local
        )
        assertEquals(expectedPluginStatus, pluginStatus)
    }

    @Test
    fun pluginStatusToPluginStatusEntityTest() {
        val fakePluginStatus = PluginStatus(
            pluginId = "plugin-power-menu",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Local
        )

        val pluginStatusEntity = fakePluginStatus.toPluginStatusEntity()

        val expectedPluginStatusEntity = PluginStatusEntity(
            pluginId = "plugin-power-menu",
            isEnabled = true,
            state = PluginState.Great,
            origin = PluginOrigin.Local
        )
        assertEquals(expectedPluginStatusEntity, pluginStatusEntity)
    }
}
