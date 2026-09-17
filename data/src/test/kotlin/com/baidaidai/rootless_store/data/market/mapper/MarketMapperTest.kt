package com.baidaidai.rootless_store.data.market.mapper

import com.baidaidai.rootless_store.data.market.mapper.MarketMapper.toMarketPageResponse
import com.baidaidai.rootless_store.data.market.mapper.MarketMapper.toMarketPaginationConfig
import com.baidaidai.rootless_store.data.market.remote.dto.MarketPageResponseDto
import com.baidaidai.rootless_store.data.market.remote.dto.MarketPaginationDto
import com.baidaidai.rootless_store.domain.market.model.MarketPageResponse
import com.baidaidai.rootless_store.domain.market.model.MarketPaginationConfig
import com.baidaidai.rootless_store.domain.plugin.manifest.PluginManifest
import com.baidaidai.rootless_store.domain.plugin.model.PluginRunModel
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketMapperTest {

    @Test
    fun marketPageResponseDtoToMarketPageResponseTest() {
        val fakePluginManifest = PluginManifest(
            installedVersion = "1.2.3",
            pluginRenderingName = "Power Menu",
            pluginPackageName = "com.example.power.menu",
            pluginId = "plugin-power-menu",
            iconUri = null,
            author = "Rootless Store",
            pluginDescription = "Adds power actions.",
            requiredEnvironment = ExecutionContext.ADB,
            pluginRunModel = PluginRunModel.OneTime,
            entryPoint = "index.sh"
        )
        val fakeMarketPageResponseDto = MarketPageResponseDto(
            manifests = listOf(fakePluginManifest),
            pagination = MarketPaginationDto(
                limit = 20,
                hasMore = true
            )
        )

        val marketPageResponse = fakeMarketPageResponseDto.toMarketPageResponse()

        val expectedMarketPageResponse = MarketPageResponse(
            manifests = listOf(fakePluginManifest),
            pagination = MarketPaginationConfig(
                limit = 20,
                hasMore = true
            )
        )
        assertEquals(expectedMarketPageResponse, marketPageResponse)
    }

    @Test
    fun marketPaginationDtoToMarketPaginationConfigTest() {
        val fakeMarketPaginationDto = MarketPaginationDto(
            limit = 20,
            hasMore = true
        )

        val marketPaginationConfig = fakeMarketPaginationDto.toMarketPaginationConfig()

        val expectedMarketPaginationConfig = MarketPaginationConfig(
            limit = 20,
            hasMore = true
        )
        assertEquals(expectedMarketPaginationConfig, marketPaginationConfig)
    }
}
