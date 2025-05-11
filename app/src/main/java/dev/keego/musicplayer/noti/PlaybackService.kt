package dev.keego.musicplayer.noti

import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import dev.keego.musicplayer.R
import dev.keego.musicplayer.local.ExoPlayerExceptionHandler
import dev.keego.musicplayer.remote.search.OnlineSongRepository
import dev.keego.musicplayer.stuff.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    private val customCommandFavorites = SessionCommand(ACTION_FAVORITES, Bundle.EMPTY)
    private var mediaSession: MediaSession? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var onlineSongRepository: OnlineSongRepository

    private lateinit var playbackManager: PlaybackManager

    private val localBroadcastReceiver by lazy {
        LocalBroadcastManager.getInstance(
            applicationContext
        )
    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        val command = CommandButton.Builder()
            .setDisplayName("Save to favorites")
            .setIconResId(R.drawable.ic_favorite)
            .setSessionCommand(customCommandFavorites)
            .build()

        /**
         * From DownloadService
         *
         */
        val cacheDataSourceFactory: DataSource.Factory =
            DemoUtil.getDataSourceFactory(this)

        Log.setLogLevel(Log.LOG_LEVEL_ERROR)

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this).setDataSourceFactory(
                    cacheDataSourceFactory
                )
            )
            .build()

        player.addListener(ExoPlayerExceptionHandler {
            localBroadcastReceiver.sendBroadcast(
                Intent(INTENT_EXO_PLAYER_EXCEPTION).putExtra(
                    INTENT_EXO_PLAYER_EXCEPTION, it
                )
            )
        })

        playbackManager = PlaybackManager(
            coroutineScope = scope,
            onlineSongRepository = onlineSongRepository,
            player = player,
            onException = {
                localBroadcastReceiver.sendBroadcast(
                    Intent(INTENT_EXO_PLAYER_EXCEPTION).putExtra(
                        INTENT_EXO_PLAYER_EXCEPTION, it
                    )
                )
            },
            onRemoteSongResolved = {}
        )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MyCallBack())
            .setCustomLayout(ImmutableList.of(command))
            .build()

        instance = this
    }

    private inner class MyCallBack : MediaSession.Callback {
        @UnstableApi
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): ConnectionResult {
            // Set available player and session commands.
            return AcceptedResultBuilder(session)
                .setAvailablePlayerCommands(
                    ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                        .remove(COMMAND_SEEK_TO_NEXT)
                        .remove(COMMAND_SEEK_TO_PREVIOUS)
                        .build()
                ).setAvailableSessionCommands(
                    ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                        .add(customCommandFavorites)
                        .build()
                )
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == ACTION_FAVORITES) {
                // Do custom logic here
                // saveToFavorites(session.player.currentMediaItem)
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            return super.onCustomCommand(session, controller, customCommand, args)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        instance = null
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        job.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_FAVORITES = "action_favorites"
        const val INTENT_EXO_PLAYER_EXCEPTION = "intent_exo_player_exception"

        private var instance: PlaybackService? = null

        fun getPlaybackManager(): PlaybackManager = instance?.playbackManager!!
    }
}