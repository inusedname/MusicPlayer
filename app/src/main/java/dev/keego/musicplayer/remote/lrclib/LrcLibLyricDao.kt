package dev.keego.musicplayer.remote.lrclib

import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Credit to https://lrclib.net/docs
 */
interface LrcLibLyricDao {

    @GET("api/get")
    suspend fun getBestMatch(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("album_name") albumName: String,
        @Query("duration") duration: Int,
    ): Response<BestMatchResultPOJO>

    @GET("api/search")
    suspend fun search(
        @Query("q") query: String
    ): Response<List<BestMatchResultPOJO>>

    companion object {
        fun build(): LrcLibLyricDao {
            val httpClient = okhttp3.OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("User-Agent", "MusicPlayer v1.0.0 (https://github.com/inusedname)")
                    .build()
                chain.proceed(request)
            }
            return Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("https://lrclib.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(LrcLibLyricDao::class.java)
        }
    }
}