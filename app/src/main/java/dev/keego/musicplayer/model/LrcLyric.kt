package dev.keego.musicplayer.model

data class LrcLyric(
    val content: Map<Timestamp, String>,
) {
    data class Timestamp(
        val minute: Int,
        val second: Int,
        val centiSeconds: Int,
    )

    companion object {
        fun fromText(raw: String): LrcLyric {
            val mutableMap = mutableMapOf<Timestamp, String>()
            val pattern = "\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\](.+)"
            val matcher = Regex(pattern)
            matcher.findAll(raw).forEach {
                mutableMap[Timestamp(
                    it.groupValues[1].toInt(),
                    it.groupValues[2].toInt(),
                    it.groupValues[3].toInt()
                )] = it.groupValues[4]
            }
            return LrcLyric(mutableMap.toMap())
        }
    }
}