package com.baidaidai.rootless_store.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baidaidai.rootless_store.data.database.repository.RoomConvertRepositoryImpl
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusDAO
import com.baidaidai.rootless_store.data.execute.database.PluginExecuteStatusEntry
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceDAO
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.plugin.room.EnvironmentInfoDAO
import com.baidaidai.rootless_store.data.plugin.room.EnvironmentInfoEntity
import com.baidaidai.rootless_store.data.plugin.room.PluginInfoDAO
import com.baidaidai.rootless_store.data.plugin.room.PluginInfoEntity
import com.baidaidai.rootless_store.data.source.database.PluginSourceDAO
import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity

@Database(
    entities = [
        PluginInfoEntity::class,
        PluginSourceEntity::class,
        PluginExecuteStatusEntry::class,
        EnvironmentInfoEntity::class,
        NotificationPreferenceEntity::class
        // 其它表也一起加进来
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(RoomConvertRepositoryImpl::class)
abstract class RootlessStoreDatabase : RoomDatabase() {
    abstract fun pluginInfoDao(): PluginInfoDAO
    abstract fun environmentInfoDao(): EnvironmentInfoDAO
    abstract fun pluginSourceDao(): PluginSourceDAO
    abstract fun pluginExecuteStatusDao(): PluginExecuteStatusDAO
    abstract fun notificationPreferenceDao(): NotificationPreferenceDAO

    // 其它 DAO 也在这里暴露
}


//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun provideDatabase(
//        @ApplicationContext context: Context
//    ): AppDatabase =
//        Room.databaseBuilder(context, AppDatabase::class.java, "rootless_store.db")
//            .build()
//
//    @Provides
//    fun providePluginDao(db: AppDatabase): PluginInfoDAO = db.pluginDao()
//}
//
//@Composable
//fun e(){
//    val k = Room.databaseBuilder(
//        context = RootLessStoreLocalContext.current,
//        klass = AppDatabase::class.java,
//        name = "pluginInfoDataBase"
//    ).build()
//}