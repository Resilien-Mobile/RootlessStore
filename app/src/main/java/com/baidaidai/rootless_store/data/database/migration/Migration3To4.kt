package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE `PluginExecuteStatusEntry`
                ADD COLUMN `executeContext` TEXT NOT NULL DEFAULT 'LIMITED'
            """.trimIndent()
        )
    }
}