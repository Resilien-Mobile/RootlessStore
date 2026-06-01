package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `NotificationPreferenceEntity` (
                    `apiKey` TEXT NOT NULL,
                    `criticalWarning` INTEGER NOT NULL,
                    PRIMARY KEY(`apiKey`)
                )
            """.trimIndent()
        )
    }
}
