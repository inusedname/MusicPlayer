package dev.keego.musicplayer.ui.home

import android.content.ComponentName
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.noti.PlaybackService
import dev.keego.musicplayer.remote.youtube.YoutubeExtractor
import dev.keego.musicplayer.ui.PlayerVMEvent
import dev.keego.musicplayer.ui.PlayerViewModel
import dev.keego.musicplayer.ui.player.LyricViewModel
import dev.keego.musicplayer.ui.player.player_
import dev.keego.musicplayer.ui.search.SearchEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.InfoItem
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType
import org.schabi.newpipe.extractor.timeago.patterns.it
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.core.net.toUri

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun HomeScreen(navController: NavController, playerViewModel: PlayerViewModel) {
    val vimel = hiltViewModel<HomeVimel>()
    val songs by vimel.songs.collectAsStateWithLifecycle()

    Scaffold(
        topBar = topBar(vimel::fetch),
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(songs) {
                    _song(it) {
                        playerViewModel.playImmediate(
                            streamable = it, false
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
private fun topBar(refresh: () -> Unit) = @Composable {
    TopAppBar(
        title = { Text(text = "All Songs") },
        actions = {
            IconButton(onClick = refresh) {
                Icon(Icons.Rounded.Refresh, null)
            }
        },
    )
}