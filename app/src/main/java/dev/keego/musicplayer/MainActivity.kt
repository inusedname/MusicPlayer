package dev.keego.musicplayer

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.noti.PlaybackService
import dev.keego.musicplayer.ui.PlayerVMEvent
import dev.keego.musicplayer.ui.PlayerViewModel
import dev.keego.musicplayer.ui.home.AppBottomNavigation
import dev.keego.musicplayer.ui.home.HomeScreen
import dev.keego.musicplayer.ui.home._dockedPlayer
import dev.keego.musicplayer.ui.player.LyricViewModel
import dev.keego.musicplayer.ui.player.player_
import dev.keego.musicplayer.ui.search.SearchScreen
import dev.keego.musicplayer.ui.setting.setting_
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import timber.log.Timber

sealed class Route {

    @Serializable
    object Home : Route()

    @Serializable
    object Search : Route()

    @Serializable
    object Library : Route()
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicPlayerTheme {
                val homeNavController = rememberNavController()
                val lyricViewModel = hiltViewModel<LyricViewModel>()
                val scope = rememberCoroutineScope()
                val shareViewModel = viewModel<PlayerViewModel>()
                val lifecycleOwner = LocalLifecycleOwner.current
                val player = remember {
                    val token = SessionToken(this, ComponentName(this, PlaybackService::class.java))
                    MediaController.Builder(this, token).buildAsync()
                }
                var song by remember { mutableStateOf<Song?>(null) }
                var isFavorite by remember { mutableStateOf(false) }
                var showFullScreenPlayer by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    scope.launch {
                        shareViewModel.event.collect { event ->
                            when (event) {
                                is PlayerVMEvent.PlayImmediate -> {
                                    (event.streamable as? Song)?.let {
                                        song = it
                                        Timber.d("song: $it")
                                        lyricViewModel.queryLyric(it)
                                    }
                                    player.get().setMediaItem(
                                        MediaItem.Builder().setMimeType(event.streamable.getMimeType())
                                            .setUri(
                                                event.streamable.getStreamUri()
                                            ).build()
                                    )
                                    player.get().prepare()
                                    player.get().play()
                                }
                            }
                        }
                    }
                }

                DisposableEffect(lifecycleOwner) {
                    val lifecycleEventObserver = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_STOP -> player.get().pause()
                            Lifecycle.Event.ON_DESTROY -> player.get().release()
                            else -> Unit
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
                    }
                }

                Scaffold(
                    bottomBar = AppBottomNavigation(homeNavController)
                ) { paddingValues ->
                    Box(Modifier.padding(paddingValues).fillMaxSize()) {
                        NavHost(
                            modifier = Modifier,
                            startDestination = Route.Home::class,
                            navController = homeNavController
                        ) {
                            composable<Route.Home> {
                                HomeScreen(homeNavController, shareViewModel)
                            }
                            composable<Route.Search>() {
                                SearchScreen()
                            }
                            composable<Route.Library>() {
                                setting_()
                            }
                        }

                        song?.let {
                            _dockedPlayer(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(12.dp),
                                player = player.get(),
                                song = it,
                                favorite = isFavorite,
                                onFavorite = { isFavorite = !isFavorite },
                                onClick = { showFullScreenPlayer = true }
                            )
                        }
                    }
                    if (showFullScreenPlayer && song != null) {
                        player_(
                            lyricViewModel = lyricViewModel,
                            song = song!!,
                            player = player.get(),
                            favorite = isFavorite,
                            favoriteClick = { isFavorite = !isFavorite }
                        ) {
                            showFullScreenPlayer = false
                        }
                    }
                }
            }
        }
    }
}