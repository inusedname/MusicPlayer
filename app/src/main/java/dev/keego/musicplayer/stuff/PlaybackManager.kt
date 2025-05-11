package dev.keego.musicplayer.stuff

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.collection.LruCache
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.local.playlist.PlaylistWithTracksTbl
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.noti.PlaybackService
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class PlayerState(
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
)

class PlaybackManager(
    private val coroutineScope: CoroutineScope,
    val player: Player,
    private val onlineSongRepository: OnlineSongRepository,
    private val onException: (Throwable) -> Unit,
    private val onRemoteSongResolved: (Song) -> Unit,
) {
    private val playbackQueue = mutableListOf<String>()
    private val streamMetadataCache = LruCache<String, Song>(10)

    val currentSong = MutableStateFlow<Song?>(null)
    val currentState = MutableStateFlow(PlayerState())

    fun release() {
        playbackQueue.clear()
        streamMetadataCache.evictAll()
    }

    init {
        initPlayer()
    }

    private fun initPlayer() {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                (mediaItem?.localConfiguration?.uri?.toString() ?: "").let {
                    currentSong.value = streamMetadataCache[it]
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                currentState.value = PlayerState(
                    hasNext = player.hasNextMediaItem(),
                    hasPrevious = player.hasPreviousMediaItem()
                )
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
            }
        })

        coroutineScope.launch {
            while(true) {
                delay(5000)
                if (playbackQueue.isNotEmpty() && !player.hasNextMediaItem()) {
                    prepareNextSong()
                }
            }
        }
    }

    private suspend fun prepareNextSong() {
        if (playbackQueue.isEmpty()) return
        val song = streamMetadataCache.get(playbackQueue[0]) ?: run {
            fetchAndCacheStreamInfo()?.also {
                onRemoteSongResolved(it)
            }
        }
        if (song == null) return
        if (playbackQueue.isEmpty()) return
        playbackQueue.removeAt(0)
        val mediaItem = MediaItem.Builder()
            .setUri(song.getStreamUri())
            .setMimeType(song.getMimeType())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtworkUri(song.thumbnailUri?.toUri())
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .build()
            )
            .build()
        player.addMediaItem(mediaItem)
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
            player.play()
        }
    }

    private suspend fun fetchAndCacheStreamInfo(): Song? {
        val res = onlineSongRepository.getYoutubeMusicStream(playbackQueue[0])
        return res.onSuccess {
            streamMetadataCache.put(it.getStreamUri().toString(), it)
        }.onFailure {
            onException(it)
        }.getOrNull()
    }

    fun addSingle(searchEntry: SearchEntry) {
        playbackQueue.add(searchEntry.url)
    }

    suspend fun playImmediately(searchEntry: SearchEntry) {
        playbackQueue.add(searchEntry.url)
        player.clearMediaItems()
        prepareNextSong()
        player.play()
    }

    suspend fun playImmediately(song: Song) {
        playbackQueue.add(song.getStreamUri().toString())
        streamMetadataCache.put(song.getStreamUri().toString(), song)
        player.clearMediaItems()
        prepareNextSong()
        player.play()
    }

    fun hasNextTrack(): Boolean {
        return playbackQueue.size > 1
    }

    suspend fun nextTrack() {
        if (playbackQueue.size > 1) {
            prepareNextSong()
            player.seekToNext()
        }
    }

    fun addFromPlaylist(playlistWithTracksTbl: PlaylistWithTracksTbl) {
        playbackQueue.clear()
        playbackQueue.addAll(playlistWithTracksTbl.tracks.map {
            it.content
        })
    }

    fun playList(playlist: PreparedPlaylist) {
        // TODO
    }

    val currentMediaItem: MediaItem? get() = player.currentMediaItem
}