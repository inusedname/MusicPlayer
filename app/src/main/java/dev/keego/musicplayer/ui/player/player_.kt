package dev.keego.musicplayer.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView
import coil.compose.AsyncImage
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.model.Song

@UnstableApi
@Composable
fun player_(
    modifier: Modifier = Modifier,
    song: Song,
    player: Player,
    favorite: Boolean,
    favoriteClick: (Boolean) -> Unit
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        Column(Modifier.padding(horizontal = 12.dp)) {
            AsyncImage(
                song.albumUri, null, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Column {
                Column {
                    Text(text = song.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = song.artist ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row {
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
                AndroidView(factory = {
                    PlayerControlView(it).apply {
                        setPlayer(player)
                    }
                })
            }
            Column {
                Text(
                    text = "LYRICS",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
                LazyColumn(
                    Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(Shapes.roundedCornerShape)
                        .background(MaterialTheme.colorScheme.primary),
                ) {
                    item {
                        Text(text = "No lyrics found")
                    }
                }
            }
        }
    }
}