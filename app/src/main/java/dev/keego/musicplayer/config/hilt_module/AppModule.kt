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
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.remote.youtube.YoutubeExtractor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideLyricRepository(
        @ApplicationContext context: Context,
        baseOkHttpClient: OkHttpClient,
        localDao: LocalLyricDao,
    ): LyricRepository {
        return LyricRepository(LrcLibLyricDao.build(baseOkHttpClient, context), localDao)
    }

    @Provides
    @Singleton
    fun provideOnlineSongRepository(youtubeExtractor: YoutubeExtractor): OnlineSongRepository {
        return OnlineSongRepository(youtubeExtractor)
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

    @Provides
    @Singleton
    fun provideYoutubeExtractor(okHttpClient: OkHttpClient) = YoutubeExtractor(okHttpClient)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
}