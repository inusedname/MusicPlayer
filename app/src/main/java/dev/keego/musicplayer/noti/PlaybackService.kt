package dev.keego.musicplayer.noti

import android.os.Bundle
import androidx.media3.common.Player.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dev.keego.musicplayer.R

@UnstableApi
class PlaybackService: MediaSessionService() {
    private val customCommandFavorites = SessionCommand(ACTION_FAVORITES, Bundle.EMPTY)
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val command = CommandButton.Builder()
            .setDisplayName("Save to favorites")
            .setIconResId(R.drawable.ic_favorite)
            .setSessionCommand(customCommandFavorites)
            .build()
        val player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MyCallBack())
            .setCustomLayout(ImmutableList.of(command))
            .build()
    }

    private inner class MyCallBack : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ConnectionResult {
            // Set available player and session commands.
            return AcceptedResultBuilder(session)
                .setAvailablePlayerCommands(
                    ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                        .remove(COMMAND_SEEK_TO_NEXT)
                        .remove(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                        .remove(COMMAND_SEEK_TO_PREVIOUS)
                        .remove(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
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
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == ACTION_FAVORITES) {
                // Do custom logic here
                // saveToFavorites(session.player.currentMediaItem)
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            return super.onCustomCommand(session, controller, customCommand, args)
        }
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        TODO("Not yet implemented")
    }

    companion object {
        const val ACTION_FAVORITES = "action_favorites"
    }
}