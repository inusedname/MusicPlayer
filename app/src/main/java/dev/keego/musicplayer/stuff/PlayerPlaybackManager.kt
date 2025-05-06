package dev.keego.musicplayer.stuff

import android.content.ComponentName
import android.content.Context
import android.media.session.PlaybackState
import androidx.annotation.OptIn
import androidx.collection.LruCache
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors
import dev.keego.musicplayer.local.playlist.PlaylistWithTracksTbl
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.noti.PlaybackService
import dev.keego.musicplayer.remote.Streamable
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType
import timber.log.Timber
import kotlin.math.min

class PlayerPlaybackManager(
    private val coroutineScope: CoroutineScope,
    private val onlineSongRepository: OnlineSongRepository,
    private val onException: (Throwable) -> Unit,
) {
    private val playbackQueue = mutableListOf<String>()
    private val streamMetadataCache = LruCache<String, Song>(10)

    private val _player = MutableStateFlow<Player?>(null)
    val playerFlow = _player.asStateFlow()

    private val player get() = _player.value

    private var nextTrackJob: Job? = null
    val currentSong = MutableStateFlow<Song?>(null)

    @OptIn(UnstableApi::class)
    fun init(context: Context) {
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        Futures.addCallback(
            future,
            object: FutureCallback<MediaController> {
                override fun onSuccess(result: MediaController?) {
                    _player.value = result
                    initPlayer()
                }

                override fun onFailure(t: Throwable) {
                    onException(t)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun release() {
        player?.release()
        _player.value = null
        nextTrackJob?.cancel()
        playbackQueue.clear()
        streamMetadataCache.evictAll()
    }

    private fun initPlayer() {
        player?.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                if (reason == Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE) {
                    // duration is available now:
                    // player.duration

                    nextTrackJob = coroutineScope.launch {
                        // on last 20 seconds of the song
                        val timeLeftToStartFetchNextTrack = min(player!!.duration - 20000, 0L)
                        delay(timeLeftToStartFetchNextTrack)
                        prepareNextSong()
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                (mediaItem?.localConfiguration?.uri?.toString() ?: "").let {
                    Timber.d("Query: $it")
                    currentSong.value = streamMetadataCache[it]

                }
            }
        })
    }

    private suspend fun prepareNextSong() {
        if (playbackQueue.size > 1) { // When player is already running
            playbackQueue.removeAt(0)
        }
        val streamable = streamMetadataCache.get(playbackQueue[0]) ?: run {
            fetchAndCacheStreamInfo()
        }
        if (streamable == null) return
        val mediaItem = MediaItem.Builder()
            .setUri(streamable.getStreamUri())
            .setMimeType(streamable.getMimeType())
            .build()
        player?.addMediaItem(mediaItem)
        if (player?.playbackState == Player.STATE_IDLE) {
            player?.prepare()
            player?.play()
        }
    }

    private suspend fun fetchAndCacheStreamInfo(): Streamable? {
        val res = onlineSongRepository.getYoutubeMusicStream(playbackQueue[0])
        return res.onSuccess {
            streamMetadataCache.put(it.getStreamUri().toString(), it)
        }.onFailure {
            onException(it)
        }.getOrNull()
    }

    fun addSingle(streamInfo: StreamInfoItem) {
        playbackQueue.add(streamInfo.url)
    }

    suspend fun playImmediately(searchEntry: SearchEntry) {
        playbackQueue.add(searchEntry.url)
        nextTrackJob?.cancelAndJoin()
        player?.clearMediaItems()
        prepareNextSong()
        player?.play()
    }

    suspend fun playImmediately(song: Song) {
        playbackQueue.add(song.getStreamUri().toString())
        streamMetadataCache.put(song.getStreamUri().toString(), song)
        nextTrackJob?.cancelAndJoin()
        player?.clearMediaItems()
        prepareNextSong()
        player?.play()
    }

    fun hasNextTrack(): Boolean {
        return playbackQueue.size > 1
    }

    fun nextTrack() {
        if (playbackQueue.size > 1) {
            nextTrackJob?.cancel()
            coroutineScope.launch {
                prepareNextSong()
                player?.seekToNext()
            }
        }
    }

    fun addFromPlaylist(playlistWithTracksTbl: PlaylistWithTracksTbl) {
        playbackQueue.clear()
        playbackQueue.addAll(playlistWithTracksTbl.tracks.map {
            it.content
        })
    }

    fun getCurrentSong(): Song? {
        return streamMetadataCache[playbackQueue.getOrNull(0) ?: ""]
    }
}