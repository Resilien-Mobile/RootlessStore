package com.baidaidai.rootless_store.data.codebrick.mapper

import com.baidaidai.rootless_store.data.codebrick.database.CodeBrickEntity
import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickConfig
import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickEntity
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeBrickMapperTest {

    @Test
    fun codeBrickEntityToCodeBrickConfigTest() {
        val fakeCodeBrickEntity = CodeBrickEntity(
            unixTimestamp = 1_725_000_000_000,
            codeBrickTitle = "Restart SurfaceFlinger",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "su -c service call SurfaceFlinger 1008",
            boundTileIndex = 2
        )

        val codeBrickConfig = fakeCodeBrickEntity.toCodeBrickConfig()

        val expectedCodeBrickConfig = CodeBrickConfig(
            unixTimestamp = 1_725_000_000_000,
            codeBrickTitle = "Restart SurfaceFlinger",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "su -c service call SurfaceFlinger 1008",
            boundTileIndex = 2
        )
        assertEquals(expectedCodeBrickConfig, codeBrickConfig)
    }

    @Test
    fun codeBrickConfigToCodeBrickEntityTest() {
        val codeBrickConfig = CodeBrickConfig(
            unixTimestamp = 1_725_000_000_000,
            codeBrickTitle = "Restart SurfaceFlinger",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "su -c service call SurfaceFlinger 1008",
            boundTileIndex = 2
        )

        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()

        val expectedCodeBrickEntity = CodeBrickEntity(
            unixTimestamp = 1_725_000_000_000,
            codeBrickTitle = "Restart SurfaceFlinger",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "su -c service call SurfaceFlinger 1008",
            boundTileIndex = 2
        )
        assertEquals(expectedCodeBrickEntity, codeBrickEntity)
    }

}
