package dev.keego.musicplayer.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.keego.musicplayer.model.Song

@Composable
fun _song(song: Song, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = song.thumbnailUri,
            contentDescription = null,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
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