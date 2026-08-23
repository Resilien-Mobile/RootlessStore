package com.baidaidai.rootless_store.data.codebrick.repository

import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickConfig
import com.baidaidai.rootless_store.data.codebrick.mapper.CodeBrickMapper.toCodeBrickEntity
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CodeBrickRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase
) {
    private val codeBrickDao = rootlessStoreDatabase.codeBrickDao()

    // Create
    suspend fun createCodeBrickConfig(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDao.insertCodeBrick(codeBrickEntity)
    }

    // Update
    suspend fun updateCodeBrickConfig(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDao.updateCodeBrick(codeBrickEntity)
    }

    // Read
    suspend fun findCodeBrickConfig(
        unixTimestamp: Long
    ): CodeBrickConfig? {
        return codeBrickDao
            .findCodeBrickByTimestamp(unixTimestamp)
            ?.toCodeBrickConfig()
    }

    fun observeCodeBricks(): Flow<List<CodeBrickConfig>> {
        return codeBrickDao
            .observeCodeBricks()
            .map { codeBrickEntityList ->
                codeBrickEntityList.map { codeBrickEntity ->
                    codeBrickEntity.toCodeBrickConfig()
                }
            }
    }

    suspend fun findCodeBrickByTileIndex(tileIndex: Int): CodeBrickConfig?{
        val codeBrickConfig = codeBrickDao.findCodeBrickByTileIndex(tileIndex)?.toCodeBrickConfig()
        return codeBrickConfig
    }

    // Delete
    suspend fun deleteCodeBrickConfig(
        codeBrickConfig: CodeBrickConfig
    ) {
        val codeBrickEntity = codeBrickConfig.toCodeBrickEntity()
        codeBrickDao.deleteCodeBrick(codeBrickEntity)
    }
}
