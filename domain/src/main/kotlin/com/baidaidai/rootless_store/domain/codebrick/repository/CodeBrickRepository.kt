package com.baidaidai.rootless_store.domain.codebrick.repository

import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.flow.Flow

interface CodeBrickRepository {

    suspend fun addCodeBrick(
        codeBrickConfig: CodeBrickConfig
    )

    suspend fun updateCodeBrick(
        codeBrickConfig: CodeBrickConfig
    )

    suspend fun findCodeBrick(
        unixTimestamp: Long
    ): CodeBrickConfig?

    fun observeCodeBricks(): Flow<List<CodeBrickConfig>>

    suspend fun findCodeBrickByTileIndex(
        tileIndex: Int
    ): CodeBrickConfig?

    suspend fun deleteCodeBrick(
        codeBrickConfig: CodeBrickConfig
    )

}
