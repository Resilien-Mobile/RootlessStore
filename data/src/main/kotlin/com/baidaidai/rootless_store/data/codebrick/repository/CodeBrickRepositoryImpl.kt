package com.baidaidai.rootless_store.data.codebrick.repository

import com.baidaidai.rootless_store.domain.codebrick.repository.CodeBrickRepository
import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickConfig
import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickEntity
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CodeBrickRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase
): CodeBrickRepository {
    private val codeBrickDao = rootlessStoreDatabase.codeBrickDao()

    // Create
    override suspend fun addCodeBrick(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDao.insertCodeBrick(codeBrickEntity)
    }

    // Update
    override suspend fun updateCodeBrick(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDao.updateCodeBrick(codeBrickEntity)
    }

    // Read
    override suspend fun findCodeBrick(
        unixTimestamp: Long
    ): CodeBrickConfig? {
        return codeBrickDao
            .findCodeBrickByTimestamp(unixTimestamp)
            ?.toCodeBrickConfig()
    }

    override fun observeCodeBricks(): Flow<List<CodeBrickConfig>> {
        return codeBrickDao
            .observeCodeBricks()
            .map { codeBrickEntities ->
                codeBrickEntities.map { codeBrickEntity ->
                    codeBrickEntity.toCodeBrickConfig()
                }
            }
    }

    override suspend fun findCodeBrickByTileIndex(tileIndex: Int): CodeBrickConfig?{
        val codeBrickConfig = codeBrickDao.findCodeBrickByTileIndex(tileIndex)?.toCodeBrickConfig()
        return codeBrickConfig
    }

    // Delete
    override suspend fun deleteCodeBrick(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDao.deleteCodeBrick(codeBrickEntity)
    }
}
