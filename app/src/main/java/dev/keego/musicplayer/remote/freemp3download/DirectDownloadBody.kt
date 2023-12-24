package dev.keego.musicplayer.remote.freemp3download

/**
 * @param f Download Quality. Must obtained from [DownQuality.text].
 * @param h Token. Must obtained from flow: [DirectDownloadDao.auth].
 * @param i Song ID. Must obtained from flow: [DirectDownloadDao.search].
 */
data class DirectDownloadBody(
    val f: String = DownQuality.MP3128.text,
    val h: String = "03AFcWeA7pCYRTVArNBUONyEFqlh0X5PR5xG2tAdJBc11o4w4ZmVegmY0vZvyxIdW6iI76T-bNP_r8ZVLF-Lcb9L1mpFFrm6ee-zKhlOOGmwrnkSrs-aQHdHYyuCbwe6uCCv_uwNW9KtJFV0YjgbTCTCj9H3yW-Nlspn0EUs7kSK0ldbhdnku8nvmp_ToP_V4Uxeu3UI2l9Y6cV7khN1Zh9eySVVAoQqlu4NuujLof1V3crlwWXEcH7pTVXCR9osNpRCIWt5d1SqzwR8na-Y2MQmqqlHwGLEHZR1hPGSgv4s8P3JIUoEFRsbF4U61hRdvBcVALLCoRZHPaoGSJageaZQ0-cn6hEciSbNPZQuGcPkZotbs3Ck1ASJBlBcmo5ikExoNHrCXOq5ROEJxdKnCiSdyBAzhUAszocA-3-ssrvpa7d6OL7wH4Ih-3_qQ_BPD48KDqETKPMPn78grUkDipZgKwuUtNPn_L5lUoHy4xhd28b3K5IeMhKfxhSnTpD89bTzXvpXULTIYM7-7xqPnLF3ZkE2G4sUr2PiLKgICKE8V3j9nq9R6DuW-j3_RQ5OHIbV_ShzXoZEvz6HL8cxvLSR2jDEExA459zA",
    val i: Int,
)