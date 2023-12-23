package dev.keego.musicplayer.ui.home

import android.content.ComponentName
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.noti.PlaybackService
import dev.keego.musicplayer.stuff.*
import dev.keego.musicplayer.ui.player.PlayerVimel
import dev.keego.musicplayer.ui.player.player_

@RootNavGraph(start = true)
@Destination
@UnstableApi
@Composable
fun home_(navigator: DestinationsNavigator) {
    val activity = LocalContext.current as ComponentActivity
    val vimel = hiltViewModel<HomeVimel>()
    val playerVimel = hiltViewModel<PlayerVimel>()
    val songs by vimel.songs.collectAsStateWithLifecycle()

    val player = remember {
        val token = SessionToken(activity, ComponentName(activity, PlaybackService::class.java))
        MediaController.Builder(activity, token).buildAsync()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
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

    var song by remember { mutableStateOf<Song?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var showFullScreenPlayer by remember { mutableStateOf(false) }

    LaunchedEffect(song) {
        song?.let {
            playerVimel.queryLyric(it)
        }
    }

    LaunchedEffect(true) {
        MediaPermission
            .onGranted {
                activity.copySampleAssetsToInternalStorage()
                vimel.fetch()
            }
            .request(activity)
    }

    Scaffold(topBar = topBar(vimel::fetch), bottomBar = botNav(navigator)) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(songs) {
                    _song(it) {
                        song = it
                        player.get().setMediaItem(MediaItem.fromUri(it.data))
                        player.get().prepare()
                        player.get().play()
                    }
                }
            }
            song?.let {
                _dockedPlayer(
                    modifier = Modifier.padding(12.dp),
                    player = player.get(),
                    song = it,
                    favorite = isFavorite,
                    onFavorite = { isFavorite = !isFavorite },
                    onClick = { showFullScreenPlayer = true }
                )
            }
        }
    }

    if (showFullScreenPlayer && song != null) {
        player_(
            playerVimel = playerVimel,
            song = song!!,
            player = player.get(),
            favorite = isFavorite,
            favoriteClick = { isFavorite = !isFavorite }
        ) {
            showFullScreenPlayer = false
        }
    }
}

@Composable
fun _dockedPlayer(
    modifier: Modifier = Modifier,
    player: Player,
    song: Song,
    favorite: Boolean,
    onFavorite: () -> Unit,
    onClick: () -> Unit,
) {
    val progress by player.progressAsState()
    val isPlaying by player.playbackAsState()

    Column(
        modifier
            .fillMaxWidth()
            .clip(Shapes.roundedCornerShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
            AsyncImage(
                model = song.albumUri,
                null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(Shapes.roundedCornerShape)
            )
            Column(
                Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(text = song.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text(text = song.artist, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    if (favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    null,
                    tint = if (favorite) Color.Red else Color.White
                )
            }
            IconButton(onClick = if (isPlaying) player::pause else player::play) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null,
                    tint = Color.White
                )
            }
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            trackColor = Color.White,
            color = Color.Black,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun _song(song: Song, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(Shapes.roundedCornerShape)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = song.albumUri,
            contentDescription = null,
            modifier = Modifier
                .clip(Shapes.roundedCornerShape)
                .fillMaxWidth(0.2f)
                .aspectRatio(1f)
        )
        Column(Modifier.padding(start = 24.dp)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .alpha(0.8f)
            )
        }
    }
}

fun botNav(navigator: DestinationsNavigator) = @Composable {
    var currentSelect by remember { mutableIntStateOf(0) }
    NavigationBar(containerColor = Color.Transparent, contentColor = Color.White) {
        NavigationBarItem(selected = currentSelect == 0, onClick = { currentSelect = 0 }, icon = {
            Icon(Icons.Outlined.Home, null)
        }, label = { Text(text = "Home") })
        NavigationBarItem(selected = currentSelect == 1, onClick = { currentSelect = 1 }, icon = {
            Icon(Icons.Outlined.Search, null)
        }, label = { Text(text = "Search") })
        NavigationBarItem(selected = currentSelect == 2, onClick = { currentSelect = 2 }, icon = {
            Icon(Icons.Outlined.Folder, null)
        }, label = { Text(text = "Library") })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun topBar(onBack: () -> Unit) = @Composable {
    TopAppBar(
        title = { Text(text = "All Songs") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, null)
            }
        }
    )
}