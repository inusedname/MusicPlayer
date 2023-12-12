package dev.keego.musicplayer.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.playbackAsState
import dev.keego.musicplayer.stuff.progressAsState

@UnstableApi
@Composable
fun player_(
    song: Song,
    player: Player,
    favorite: Boolean,
    favoriteClick: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            _player_content(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                song = song,
                player = player,
                favorite = favorite,
                favoriteClick = favoriteClick,
                closeClick = onDismiss
            )
        }
    }
}

@UnstableApi
@Composable
private fun _player_content(
    modifier: Modifier = Modifier,
    song: Song,
    player: Player,
    favorite: Boolean,
    favoriteClick: (Boolean) -> Unit,
    closeClick: () -> Unit,
) {
    Column(
        modifier
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
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Column {
                Column {
                    Text(text = song.title, style = MaterialTheme.typography.headlineMedium)
                    Row {
                        Text(
                            text = song.artist ?: "Unknown",
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
            Column {
                Text(
                    text = "LYRICS",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
                Box {
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(Shapes.roundedCornerShape)
                            .background(MaterialTheme.colorScheme.primary),
                    ) {

                    }
                    Text(
                        text = "No lyrics found",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun _controller(modifier: Modifier = Modifier, player: Player) {
    val progress by player.progressAsState()
    val playing by player.playbackAsState()

    var userSeekProgress by remember {
        mutableFloatStateOf(0f)
    }

    Column(modifier) {
        Slider(value = progress, onValueChangeFinished = {
            player.seekTo((userSeekProgress * player.duration).toLong())
        }, onValueChange = { userSeekProgress = it })
        Row {
            Text(text = "0:00", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.weight(1f))
            Text(
                text = remember { millisToHHmmSS(player.duration) },
                style = MaterialTheme.typography.labelSmall
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterHorizontally)
        ) {
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = null)
            }
            IconButton(
                onClick = if (playing) player::pause else player::play,
                modifier = Modifier.weight(1f),
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
