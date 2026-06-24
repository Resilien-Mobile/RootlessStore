package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `CodeBrickEntity` (
                    `unixTimeStamp` INTEGER NOT NULL,
                    `codeBrickTitle` TEXT NOT NULL,
                    `codeBrickEnvironment` TEXT NOT NULL,
                    `codeBrickContent` TEXT NOT NULL,
                    PRIMARY KEY(`unixTimeStamp`)
                )
            """.trimIndent()
        )
    }
}
