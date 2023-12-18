package dev.keego.musicplayer.remote

import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LyricDao {

    @GET("search")
    suspend fun search(@Query("q") text: String): Response<GeniusSearchPOJO>

    companion object {
        fun build(): LyricDao {
            val httpClient = okhttp3.OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${Constants.GENIUS_ACCESS_TOKEN}")
                    .build()
                chain.proceed(request)
            }
            return Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("https://api.genius.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(LyricDao::class.java)
        }
    }
}