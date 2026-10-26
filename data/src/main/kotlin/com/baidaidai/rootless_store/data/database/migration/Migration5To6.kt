package com.baidaidai.rootless_store.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(5, 6) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // Create the CA certificate table
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `CaCertificateEntity` (
                    `unixTimestamp` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `derBytes` BLOB NOT NULL,
                    `enabled` INTEGER NOT NULL,
                    PRIMARY KEY(`unixTimestamp`)
                )
            """.trimIndent()
        )
    }
}
