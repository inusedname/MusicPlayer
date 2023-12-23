package dev.keego.musicplayer.model

import dev.keego.musicplayer.local.Lyric

/**
 * @param content: Map of Timestamp (as Millisec) and Lyric
 */
data class LrcLyric(
    val content: Map<Int, String>,
) {
    companion object {
        private fun fromText(raw: String): LrcLyric {
            val mutableMap = mutableMapOf<Int, String>()
            val pattern = "\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\](.+)"
            val matcher = Regex(pattern)
            matcher.findAll(raw).forEach {
                mutableMap[
                    it.groupValues[1].toInt() * 60 * 1000 +
                    it.groupValues[2].toInt() * 1000 +
                    it.groupValues[3].toInt() * 10
                ] = it.groupValues[4]
            }
            return LrcLyric(mutableMap.toMap())
        }

        fun fromLyric(lyric: Lyric): LrcLyric {
            return fromText(lyric.lrcContent)
        }
    }
}