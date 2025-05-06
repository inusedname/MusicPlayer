package dev.keego.musicplayer.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.PlayerState
import dev.keego.musicplayer.stuff.playbackAsState
import dev.keego.musicplayer.stuff.progressAsState
import dev.keego.musicplayer.stuff.progressMsAsState
import dev.keego.musicplayer.ui.UiState
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    song: Song,
    player: Player,
    playerState: PlayerState,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface {
            PlayerScreenContent(song, player, playerState, onDismiss)
        }
    }
}

@Composable
private fun PlayerScreenContent(
    song: Song,
    player: Player,
    playerState: PlayerState,
    onClickClose: () -> Unit,
) {
    var isFavorite by remember { mutableStateOf(false) }
    var volume by remember { mutableFloatStateOf(80f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClickClose) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Close"
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            IconButton(onClick = { /* Show more options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Album Art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = song.thumbnailUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Song Info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Bar, Main controls
        _controller(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(1f),
            player = player,
            playerState = playerState,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Volume and Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Volume control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val VolumeIcon = when {
                    volume == 0f -> Icons.Default.VolumeMute
                    volume < 50f -> Icons.Default.VolumeDown
                    else -> Icons.Default.VolumeUp
                }

                IconButton(onClick = { /* Toggle mute */ }) {
                    Icon(
                        imageVector = VolumeIcon,
                        contentDescription = "Volume",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Slider(
                    value = volume / 100f,
                    onValueChange = { volume = it * 100f },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { isFavorite = !isFavorite }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = { /* Share */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
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
                .clip(MaterialTheme.shapes.medium)
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
                .clip(MaterialTheme.shapes.medium)
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
fun _controller(modifier: Modifier = Modifier, player: Player, playerState: PlayerState) {
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                enabled = playerState.hasPrevious,
                onClick = { player.seekToPreviousMediaItem() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = if (playing) player::pause else player::play,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(
                enabled = playerState.hasNext,
                onClick = { player.seekToNextMediaItem() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = { /* Toggle repeat */ }) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
