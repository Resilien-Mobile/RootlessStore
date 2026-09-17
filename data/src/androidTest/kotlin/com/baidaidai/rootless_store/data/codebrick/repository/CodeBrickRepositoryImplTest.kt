package com.baidaidai.rootless_store.data.codebrick.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test


class CodeBrickRepositoryImplTest {

    private lateinit var database: RootlessStoreDatabase
    private lateinit var repository: CodeBrickRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            RootlessStoreDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = CodeBrickRepositoryImpl(database)
    }


    @Test
    fun addCodeBrick_thenCanFindIt() = runBlocking {
        val config = CodeBrickConfig(
            unixTimestamp = 1000L,
            codeBrickTitle = "Silent Mode",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "cmd audio set-ringer-mode SILENT",
            boundTileIndex = 1
        )

        repository.addCodeBrick(config)

        val result = repository.findCodeBrick(1000L)

        assertEquals(config, result)
    }

    @Test
    fun findCodeBrick_whenNotExists_returnsNull() = runBlocking {
        assertNull(repository.findCodeBrick(9999L))
    }

    @Test
    fun updateCodeBrick_thenReturnsUpdatedValue() = runBlocking {
        val original = CodeBrickConfig(
            unixTimestamp = 1000L,
            codeBrickTitle = "Original",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "original command",
            boundTileIndex = 1
        )
        val updated = original.copy(
            codeBrickTitle = "Updated",
            codeBrickContent = "updated command",
            boundTileIndex = 2
        )

        repository.addCodeBrick(original)
        repository.updateCodeBrick(updated)

        assertEquals(updated, repository.findCodeBrick(1000L))
    }

    @Test
    fun findCodeBrickByTileIndex_thenCanFindIt() = runBlocking {
        val config = CodeBrickConfig(
            unixTimestamp = 1000L,
            codeBrickTitle = "ADB Command",
            codeBrickEnvironment = ExecutionContext.ADB,
            codeBrickContent = "adb shell",
            boundTileIndex = 3
        )

        repository.addCodeBrick(config)

        assertEquals(config, repository.findCodeBrickByTileIndex(3))
    }

    @Test
    fun findCodeBrickByTileIndex_whenNotExists_returnsNull() = runBlocking {
        assertNull(repository.findCodeBrickByTileIndex(99))
    }

    @Test
    fun observeCodeBricks_afterAddingEmitsConfigs() = runBlocking {
        val config = CodeBrickConfig(
            unixTimestamp = 1000L,
            codeBrickTitle = "Observed",
            codeBrickEnvironment = ExecutionContext.ROOTD,
            codeBrickContent = "observed command",
            boundTileIndex = null
        )
        val nextEmission = async(start = CoroutineStart.UNDISPATCHED) {
            repository.observeCodeBricks()
                .drop(1)
                .first()
        }

        repository.addCodeBrick(config)

        assertEquals(listOf(config), withTimeout(2_000L) { nextEmission.await() })
    }

    @Test
    fun deleteCodeBrick_thenCannotFindIt() = runBlocking {
        val config = CodeBrickConfig(
            unixTimestamp = 1000L,
            codeBrickTitle = "Temporary",
            codeBrickEnvironment = ExecutionContext.ROOTD,
            codeBrickContent = "temporary command",
            boundTileIndex = null
        )

        repository.addCodeBrick(config)
        repository.deleteCodeBrick(config)

        assertNull(repository.findCodeBrick(1000L))
    }

    @Test
    fun addCodeBrick_withSameTimestamp_replacesExisting() = runBlocking {
        val original = CodeBrickConfig(
            unixTimestamp = 1000L,
            codeBrickTitle = "Original",
            codeBrickEnvironment = ExecutionContext.LIMITED,
            codeBrickContent = "original command",
            boundTileIndex = 1
        )
        val replacement = original.copy(
            codeBrickTitle = "Replacement",
            codeBrickContent = "replacement command",
            boundTileIndex = 2
        )

        repository.addCodeBrick(original)
        repository.addCodeBrick(replacement)

        assertEquals(replacement, repository.findCodeBrick(1000L))
    }


    @After
    fun uninstall() {
        database.close()
    }

}
