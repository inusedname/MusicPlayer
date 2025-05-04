package dev.keego.musicplayer.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
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
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
            AsyncImage(
                model = song.thumbnailUri,
                null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
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