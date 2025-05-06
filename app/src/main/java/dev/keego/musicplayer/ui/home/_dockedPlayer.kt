package dev.keego.musicplayer.ui.home

import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.playbackAsState
import dev.keego.musicplayer.stuff.progressAsState

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
//            .height(52.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
    ) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(),
            strokeCap = StrokeCap.Round
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            AsyncImage(
                model = song.thumbnailUri,
                null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop,
            )
            Column(
                Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = song.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground)
            }
            FilledIconButton(
                onClick = if (isPlaying) player::pause else player::play,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

@OptIn(UnstableApi::class)
@Preview
@Composable
fun DockedPlayerPreview() {
    val context = LocalContext.current
    _dockedPlayer(
        player = object: SimpleBasePlayer(Looper.getMainLooper()) {
            @OptIn(UnstableApi::class)
            override fun getState(): State {
                return State.Builder().build()
            }
        },
        song = Song(
            id = "1",
            title = "Song Title",
            artist = "Artist Name",
            album = "Album Name",
            duration = 300000,
            data = "data",
            thumbnailUri = null
        ),
        favorite = false,
        onFavorite = {},
        onClick = {}
    )
}