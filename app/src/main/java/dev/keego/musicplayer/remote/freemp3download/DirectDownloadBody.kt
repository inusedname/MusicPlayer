package dev.keego.musicplayer.remote.freemp3download

import dev.keego.musicplayer.pref.AppPreferences

/**
 * @param f Download Quality. Must be obtained from [DownQuality.text].
 * @param h Token. Must be obtained from flow: [TODO].
 * @param i Song ID. Must be obtained from: [SearchSongDao.search].
 */
data class DirectDownloadBody(
    val f: String = DownQuality.MP3128.text,
    val h: String = AppPreferences.directDownloadToken!!,
    val i: Int,
)