package com.baidaidai.rootless_store.data.di

import android.content.Context
import androidx.room.Room
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.database.migration.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseHiltModule {
    @Provides
    @Singleton
    fun provideRootlessStoreDataBase(
        @ApplicationContext
        context: Context
    ): RootlessStoreDatabase{
        return Room.databaseBuilder(
            context = context,
            klass = RootlessStoreDatabase::class.java,
            name = "RootlessStoreDataBase"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

}