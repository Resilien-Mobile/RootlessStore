package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // Create the new plugin source table
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `pluginSource_new` (
                    `sourceID` TEXT NOT NULL,
                    `sourceName` TEXT NOT NULL,
                    `sourceRemoteEndpoint` TEXT NOT NULL,
                    `userAccessToken` TEXT,
                    `needsAuthentication` INTEGER NOT NULL,
                    PRIMARY KEY(`sourceID`)
                )
            """.trimIndent()
        )

        // Copy existing data and rename the authentication column
        db.execSQL(
            """
                INSERT INTO `pluginSource_new` (
                    `sourceID`,
                    `sourceName`,
                    `sourceRemoteEndpoint`,
                    `userAccessToken`,
                    `needsAuthentication`
                )
                SELECT
                    `sourceID`,
                    `sourceName`,
                    `sourceRemoteEndpoint`,
                    `userAccessToken`,
                    `requireAuthentication`
                FROM `pluginSource`
            """.trimIndent()
        )

        // Replace the old plugin source table
        db.execSQL("DROP TABLE `pluginSource`")
        db.execSQL("ALTER TABLE `pluginSource_new` RENAME TO `pluginSource`")
    }
}
