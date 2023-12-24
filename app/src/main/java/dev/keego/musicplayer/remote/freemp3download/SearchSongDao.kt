package dev.keego.musicplayer.remote.freemp3download

import android.content.Context
import dev.keego.musicplayer.remote.lrclib.LrcLibLyricDao
import okhttp3.Cache
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchSongDao {

    @GET("search")
    suspend fun search(@Query("q") query: String): Response<SearchSongPOJO>

    companion object {
        fun build(context: Context): SearchSongDao {
            val cacheDir = context.cacheDir
            val cacheSize = 10L * 1024L * 1024L // 10 MiB
            val httpClient = okhttp3.OkHttpClient.Builder()
                .cache(Cache(cacheDir, cacheSize))
            httpClient.addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            return Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("https://api.deezer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(SearchSongDao::class.java)
        }
    }
}