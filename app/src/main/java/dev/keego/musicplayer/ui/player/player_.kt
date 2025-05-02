package dev.keego.musicplayer.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.playbackAsState
import dev.keego.musicplayer.stuff.progressAsState
import dev.keego.musicplayer.stuff.progressMsAsState
import dev.keego.musicplayer.ui.UiState
import dev.keego.musicplayer.ui.lyric.browse_lyrics_
import kotlinx.coroutines.delay

@UnstableApi
@Composable
fun player_(
    lyricViewModel: LyricViewModel,
    song: Song,
    player: Player,
    favorite: Boolean,
    favoriteClick: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var showBrowseLyrics by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            _player_content(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                lyricViewModel = lyricViewModel,
                song = song,
                player = player,
                favorite = favorite,
                favoriteClick = favoriteClick,
                closeClick = onDismiss
            ) {
                showBrowseLyrics = true
            }
            if (showBrowseLyrics) {
                browse_lyrics_(song = song, onDismiss = { showBrowseLyrics = false }) {
                    /*TODO*/
                }
            }
        }
    }
}

@UnstableApi
@Composable
private fun _player_content(
    modifier: Modifier = Modifier,
    lyricViewModel: LyricViewModel,
    song: Song,
    player: Player,
    favorite: Boolean,
    favoriteClick: (Boolean) -> Unit,
    closeClick: () -> Unit,
    showBrowseLyrics: () -> Unit,
) {
    Column(
        modifier
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = closeClick) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
            }
            AsyncImage(
                song.albumUri, null, modifier = Modifier
                    .clip(Shapes.roundedCornerShape)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Column {
                Column {
                    Text(text = song.title, style = MaterialTheme.typography.headlineMedium)
                    Row {
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Outlined.Share, null)
                        }
                        IconButton(onClick = { favoriteClick(!favorite) }) {
                            Icon(
                                if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                null,
                                tint = if (favorite) MaterialTheme.colorScheme.primary else Color.White
                            )
                        }
                    }
                }
                _controller(modifier = Modifier.padding(top = 8.dp), player = player)
            }
            _lyric(player, lyricViewModel, showBrowseLyrics)
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun _lyric(
    player: Player,
    lyricViewModel: LyricViewModel,
    showBrowseLyrics: () -> Unit,
) {
    val lyricUiState by lyricViewModel.lyricUiState.collectAsStateWithLifecycle()
    val lyric by lyricViewModel.lyric.collectAsStateWithLifecycle()
    val lyricTimestamps = remember(lyricUiState) {
        if (lyricUiState == UiState.SUCCESS) {
            lyric!!.content.keys.toList()
        } else {
            null
        }
    }

    val progress by player.progressMsAsState()
    val lyricScrollState = rememberLazyListState()
    var highlightedLyricLine by remember { mutableIntStateOf(0) }
    LaunchedEffect(progress) {
        highlightedLyricLine = lyricTimestamps?.indexOfLast { it <= progress } ?: 0
        delay(100)
        lyricScrollState.animateScrollToItem(if (highlightedLyricLine > 0) highlightedLyricLine - 1 else 0)
    }

    Column(
        Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        Text(
            text = "Lyrics".uppercase(),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(Shapes.roundedCornerShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            when (lyricUiState) {
                UiState.LOADING -> {
                    Column(
                        Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Fetching lyrics...",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                UiState.SUCCESS -> {
                    lyric?.let {
                        LazyColumn(
                            Modifier.fillMaxSize(),
                            userScrollEnabled = false,
                            state = lyricScrollState,
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                        ) {
                            itemsIndexed(it.content.values.toList()) { idx, line ->
                                Text(
                                    text = line,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .alpha(
                                            if (idx == highlightedLyricLine)
                                                1f else 0.5f
                                        ),
                                    color = if (idx <= highlightedLyricLine)
                                        Color.White else Color.Black,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }

                is UiState.ERROR -> {
                    Text(
                        text = (lyricUiState as UiState.ERROR).exception,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        Row(
            Modifier
                .padding(top = 8.dp)
                .clip(Shapes.roundedCornerShape)
                .clickable(onClick = showBrowseLyrics)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Not what you're looking for?",
                style = MaterialTheme.typography.labelSmall
            )
            Icon(
                Icons.Rounded.ArrowForward, null,
                Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
            )
        }
    }
}

@Composable
fun _controller(modifier: Modifier = Modifier, player: Player) {
    val progress by player.progressAsState()
    val playing by player.playbackAsState()

    var userSeeking by remember { mutableStateOf(false) }
    var userSeekProgress by remember {
        mutableFloatStateOf(0f)
    }

    Column(modifier) {
        Slider(
            /**
             * Temporary workaround
             * Finds a better way
             */
            value = if (!userSeeking) progress else userSeekProgress,
            onValueChangeFinished = {
                player.seekTo((userSeekProgress * player.duration).toLong())
                userSeeking = false
            },
            onValueChange = {
                userSeeking = true
                userSeekProgress = it
            }
        )
        Row {
            Text(
                text = millisToHHmmSS(((if (!userSeeking) progress else userSeekProgress) * player.duration).toLong()),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = remember { millisToHHmmSS(player.duration) },
                style = MaterialTheme.typography.labelSmall
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = null)
            }
            IconButton(
                onClick = if (playing) player::pause else player::play,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun millisToHHmmSS(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60)) % 24

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
