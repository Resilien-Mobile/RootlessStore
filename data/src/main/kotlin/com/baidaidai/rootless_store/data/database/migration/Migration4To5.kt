package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Create the new environment status table
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `environmentStatus` (
                    `environmentID` TEXT NOT NULL,
                    `enabled` INTEGER NOT NULL,
                    `state` TEXT NOT NULL,
                    `origin` TEXT NOT NULL,
                    PRIMARY KEY(`environmentID`)
                )
            """.trimIndent()
        )

        // 2. Move the state columns from environmentInfo into environmentStatus (source -> origin)
        db.execSQL(
            """
                INSERT INTO `environmentStatus` (`environmentID`, `enabled`, `state`, `origin`)
                SELECT `environmentID`, `enabled`, `state`, `source`
                FROM `environmentInfo`
            """.trimIndent()
        )

        // 3. Rebuild environmentInfo without the state columns
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `environmentInfo_new` (
                    `environmentID` TEXT NOT NULL,
                    `installedVersion` TEXT NOT NULL,
                    `environmentRenderingName` TEXT NOT NULL,
                    `environmentPackageName` TEXT NOT NULL,
                    `iconURI` TEXT,
                    `author` TEXT NOT NULL,
                    `environmentDescription` TEXT NOT NULL,
                    `requiredEnvironment` TEXT NOT NULL,
                    `entryPoint` TEXT NOT NULL,
                    `ldLibraryPath` TEXT NOT NULL,
                    `env` TEXT NOT NULL,
                    PRIMARY KEY(`environmentID`)
                )
            """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO `environmentInfo_new` (
                    `environmentID`,
                    `installedVersion`,
                    `environmentRenderingName`,
                    `environmentPackageName`,
                    `iconURI`,
                    `author`,
                    `environmentDescription`,
                    `requiredEnvironment`,
                    `entryPoint`,
                    `ldLibraryPath`,
                    `env`
                )
                SELECT
                    `environmentID`,
                    `installedVersion`,
                    `environmentRenderingName`,
                    `environmentPackageName`,
                    `iconURI`,
                    `author`,
                    `environmentDescription`,
                    `requiredEnvironment`,
                    `entryPoint`,
                    `ldLibraryPath`,
                    `env`
                FROM `environmentInfo`
            """.trimIndent()
        )

        // 4. Replace the old environment info table
        db.execSQL("DROP TABLE `environmentInfo`")
        db.execSQL("ALTER TABLE `environmentInfo_new` RENAME TO `environmentInfo`")
    }
}
