package dev.keego.musicplayer

import MySpaceScreen
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.noti.DemoUtil
import dev.keego.musicplayer.noti.PlaybackService
import dev.keego.musicplayer.stuff.CustomNavType
import dev.keego.musicplayer.stuff.PlaybackManager
import dev.keego.musicplayer.ui.PlayerVMEvent
import dev.keego.musicplayer.ui.PlayerViewModel
import dev.keego.musicplayer.ui.home.AppBottomNavigation
import dev.keego.musicplayer.ui.home.HomeScreen
import dev.keego.musicplayer.ui.home._dockedPlayer
import dev.keego.musicplayer.ui.player.LyricViewModel
import dev.keego.musicplayer.ui.player.PlayerScreen
import dev.keego.musicplayer.ui.playlist.PlaylistScreen
import dev.keego.musicplayer.ui.search.SearchScreen
import dev.keego.musicplayer.ui.setting.setting_
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val shareViewModel by viewModels<PlayerViewModel>()
    private val _playbackManager = MutableStateFlow<PlaybackManager?>(null)

    private val downloadTracker by lazy {
        DemoUtil.getDownloadTracker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val future = MediaController.Builder(this, sessionToken).buildAsync()

        Futures.addCallback(future, object: FutureCallback<MediaController> {
            override fun onSuccess(result: MediaController?) {
                Timber.d("MediaController built")
                _playbackManager.value = PlaybackService.getPlaybackManager()
            }

            override fun onFailure(t: Throwable) {
                shareViewModel.publishError(t)
            }
        }, MoreExecutors.directExecutor())

        setContent {
            MusicPlayerTheme {
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                val owner = LocalLifecycleOwner.current
                val homeNavController = rememberNavController()
                val lyricViewModel = hiltViewModel<LyricViewModel>()

                val playbackManager by _playbackManager.collectAsState()
                val currentSong by (playbackManager?.currentSong ?: flowOf()).collectAsState(null)
                var isFavorite by remember { mutableStateOf(false) }
                var showFullScreenPlayer by remember { mutableStateOf(false) }
                val player = playbackManager?.player
                val playerState by (playbackManager?.currentState ?: flowOf()).collectAsState(null)
                var playerError by remember { mutableStateOf<Throwable?>(null) }

                LaunchedEffect(Unit) {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        shareViewModel.event.collectLatest { event ->
                            when (event) {
                                is PlayerVMEvent.PlayImmediate -> {
                                    when {
                                        event.song != null -> playbackManager?.playImmediately(event.song)
                                        event.searchEntry != null -> playbackManager?.playImmediately(event.searchEntry)
                                    }
                                }

                                is PlayerVMEvent.PlayNext -> {
                                    when {
                                        event.searchEntry != null -> playbackManager?.addSingle(event.searchEntry)
                                    }
                                }

                                is PlayerVMEvent.PlayerError -> {
                                    playerError = event.error
                                    scope.launch { delay(1500); playerError = null }
                                }

                                is PlayerVMEvent.PlayList -> {
                                    playbackManager?.playList(event.playlist)
                                }
                            }
                        }
                    }
                }

                DisposableEffect(owner) {
                    val broadcastReceiver = object: BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            if (intent?.action == PlaybackService.INTENT_EXO_PLAYER_EXCEPTION) {
                                playerError = intent.getSerializableExtra(PlaybackService.INTENT_EXO_PLAYER_EXCEPTION) as Throwable
                                scope.launch { delay(1500); playerError = null }
                            }
                        }
                    }
                    ContextCompat.registerReceiver(context, broadcastReceiver, IntentFilter(PlaybackService.INTENT_EXO_PLAYER_EXCEPTION), ContextCompat.RECEIVER_NOT_EXPORTED)
                    onDispose {
                        context.unregisterReceiver(broadcastReceiver)
                    }
                }

                Scaffold(
                    bottomBar = AppBottomNavigation(homeNavController)
                ) { paddingValues ->
                    Box(
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        MyNavHost(homeNavController)

                        if (currentSong != null && player != null) {
                            _dockedPlayer(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                player = player,
                                playerState = playerState,
                                song = currentSong!!,
                                onClick = { showFullScreenPlayer = true }
                            )
                        }
                        if (playerError != null) {
                            Snackbar(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(8.dp),
                                action = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        modifier = Modifier
                                            .clickable {
                                                playerError = null
                                            }
                                            .padding(8.dp)
                                    )
                                },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ) {
                                Text(
                                    text = playerError!!.message ?: "Unknown error",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    if (showFullScreenPlayer && currentSong != null && player != null && playerState != null) {
                        PlayerScreen(
                            song = currentSong!!,
                            player = player,
                            playerState = playerState!!,
                            mediaItem = playbackManager?.currentMediaItem,
                            lyricViewModel = lyricViewModel,
                            downloadTracker = downloadTracker,
                        ) {
                            showFullScreenPlayer = false
                        }
                    }
                }
            }
        }
    }

    private val navTypes = mapOf(
        typeOf<PreparedPlaylist>() to CustomNavType(PreparedPlaylist.serializer())
    )

    @Composable
    private fun MyNavHost(homeNavController: NavHostController) {
        NavHost(
            startDestination = Route.Home::class,
            navController = homeNavController
        ) {
            composable<Route.Home> {
                HomeScreen(homeNavController, shareViewModel)
            }
            composable<Route.Search>() {
                SearchScreen(shareViewModel)
            }
            composable<Route.Setting>() {
                setting_()
            }
            composable<Route.MySpace>() {
                MySpaceScreen(homeNavController)
            }
            composable<Route.Playlist>(
                typeMap = navTypes
            ) {
                val route = it.toRoute<Route.Playlist>()
                PlaylistScreen(
                    playlist = route.preparedPlaylist,
                    navController = homeNavController,
                    playerViewModel = shareViewModel,
                )
            }
        }
    }

    companion object {
        const val DEBUG = true
    }
}