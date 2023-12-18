package dev.keego.musicplayer.config.hilt_module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keego.musicplayer.remote.LyricDao
import dev.keego.musicplayer.remote.LyricRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideLyricRepository(): LyricRepository {
        return LyricRepository(LyricDao.build())
    }
}