package com.baidaidai.rootless_store.data.source.mapper

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSource
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationMetadata
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceCredentials
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceEntity
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationMetadataDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationResponseDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceDto
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMetadata
import com.baidaidai.rootless_store.domain.source.model.PluginSourceCredentials
import org.junit.Assert.assertEquals
import org.junit.Test

class PluginSourceMapperTest {

    @Test
    fun pluginSourceAuthenticationMetadataDtoToPluginSourceAuthenticationMetadataTest() {
        val fakePluginSourceAuthenticationMetadataDto = PluginSourceAuthenticationMetadataDto(
            needsAuthentication = true
        )

        val pluginSourceAuthenticationMetadata =
            fakePluginSourceAuthenticationMetadataDto.toPluginSourceAuthenticationMetadata()

        val expectedPluginSourceAuthenticationMetadata = PluginSourceAuthenticationMetadata(
            needsAuthentication = true
        )
        assertEquals(expectedPluginSourceAuthenticationMetadata, pluginSourceAuthenticationMetadata)
    }

    @Test
    fun pluginSourceDtoToPluginSourceTest() {
        val fakePluginSourceDto = PluginSourceDto(
            sourceId = "official-source",
            sourceName = "Official Source",
            sourceRemoteEndpoint = "https://example.com/source.json",
            authenticationMetadata = PluginSourceAuthenticationMetadataDto(
                needsAuthentication = true
            )
        )

        val pluginSource = fakePluginSourceDto.toPluginSource()

        val expectedPluginSource = PluginSource(
            sourceId = "official-source",
            sourceName = "Official Source",
            sourceRemoteEndpoint = "https://example.com/source.json",
            pluginSourceAuthenticationMetadata = PluginSourceAuthenticationMetadata(
                needsAuthentication = true
            )
        )
        assertEquals(expectedPluginSource, pluginSource)
    }

    @Test
    fun pluginSourceEntityToPluginSourceTest() {
        val fakePluginSourceEntity = PluginSourceEntity(
            sourceId = "official-source",
            sourceName = "Official Source",
            sourceRemoteEndpoint = "https://example.com/source.json",
            accessToken = "persisted-token",
            pluginSourceAuthenticationMetadata = PluginSourceAuthenticationMetadata(
                needsAuthentication = true
            )
        )

        val pluginSource = fakePluginSourceEntity.toPluginSource()

        val expectedPluginSource = PluginSource(
            sourceId = "official-source",
            sourceName = "Official Source",
            sourceRemoteEndpoint = "https://example.com/source.json",
            pluginSourceAuthenticationMetadata = PluginSourceAuthenticationMetadata(
                needsAuthentication = true
            )
        )
        assertEquals(expectedPluginSource, pluginSource)
    }

    @Test
    fun pluginSourceToPluginSourceEntityTest() {
        val fakePluginSource = PluginSource(
            sourceId = "official-source",
            sourceName = "Official Source",
            sourceRemoteEndpoint = "https://example.com/source.json",
            pluginSourceAuthenticationMetadata = PluginSourceAuthenticationMetadata(
                needsAuthentication = true
            )
        )

        val pluginSourceEntity = fakePluginSource.toPluginSourceEntity()

        val expectedPluginSourceEntity = PluginSourceEntity(
            sourceId = "official-source",
            sourceName = "Official Source",
            sourceRemoteEndpoint = "https://example.com/source.json",
            accessToken = null,
            pluginSourceAuthenticationMetadata = PluginSourceAuthenticationMetadata(
                needsAuthentication = true
            )
        )
        assertEquals(expectedPluginSourceEntity, pluginSourceEntity)
    }

    @Test
    fun pluginSourceAuthenticationResponseDtoToPluginSourceCredentialsTest() {
        val fakePluginSourceAuthenticationResponseDto = PluginSourceAuthenticationResponseDto(
            username = "alice",
            accessToken = "access-token"
        )

        val pluginSourceCredentials =
            fakePluginSourceAuthenticationResponseDto.toPluginSourceCredentials()

        val expectedPluginSourceCredentials = PluginSourceCredentials(
            username = "alice",
            accessToken = "access-token"
        )
        assertEquals(expectedPluginSourceCredentials, pluginSourceCredentials)
    }
}
