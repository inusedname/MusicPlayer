package dev.keego.musicplayer.config.hilt_module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keego.musicplayer.local.AppDatabase
import dev.keego.musicplayer.local.LocalLyricDao
import dev.keego.musicplayer.remote.LyricRepository
import dev.keego.musicplayer.remote.lrclib.LrcLibLyricDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideLyricRepository(
        @ApplicationContext context: Context,
        localDao: LocalLyricDao,
    ): LyricRepository {
        return LyricRepository(LrcLibLyricDao.build(context), localDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.createDatabase(context)
    }

    @Provides
    @Singleton
    fun provideLocalLyricDao(database: AppDatabase): LocalLyricDao {
        return database.lyricDao()
    }
}