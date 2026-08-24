package com.baidaidai.rootless_store.data.codebrick.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeBrickDao {
    /**
     * CRUD
     */

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodeBrick(codeBrickEntity: CodeBrickEntity)

    // Update
    @Update
    suspend fun updateCodeBrick(codeBrickEntity: CodeBrickEntity)

    // Read
    @Query("SELECT * FROM CodeBrickEntity WHERE unixTimeStamp = :unixTimestamp LIMIT 1")
    suspend fun findCodeBrickByTimestamp(unixTimestamp: Long): CodeBrickEntity?

    @Query("SELECT * FROM CodeBrickEntity")
    fun observeCodeBricks(): Flow<List<CodeBrickEntity>>

    @Query("SELECT * FROM CodeBrickEntity WHERE bindTileIndex = :tileIndex LIMIT 1")
    suspend fun findCodeBrickByTileIndex(tileIndex: Int): CodeBrickEntity?

    // Delete
    @Delete
    suspend fun deleteCodeBrick(codeBrickEntity: CodeBrickEntity)
}
