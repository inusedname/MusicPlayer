package dev.keego.musicplayer.remote

import com.google.gson.annotations.SerializedName

data class GeniusSearchPOJO(
    private val response: Response
) {
    data class Response(
        val hits: List<Hit>
    ) {
        data class Hit(
            val result: Result
        ) {
            data class Result(
                @SerializedName("full_title") val fullTitle: String,
                @SerializedName("header_image_thumbnail_url") val thumbnail: String,
                val url: String
            )
        }
    }

    fun getResults() = response.hits
}