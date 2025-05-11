package dev.keego.musicplayer.ui.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.ui.PlayerViewModel
import dev.keego.musicplayer.ui.search.Provider

/**
 * screen definition:
 * - use spotify like style
 * - take advanced for both user-made and youtube actual playlist
 * - use domain.PreparedPlaylist class
 * - consists of the playlist basics: cover, title, play button, list of song
 * - also a shuffle button, a download button.
 * - artist ui: a text, if the playlist is user-made then displayed "You", otherwise display Artist name and mark it as clickable. Let the action as TODO i will implement my self
 * - please update the PlaylistTbl and related DAO to have an addition "Provider" field. Use the provider in ui.search package
 */

interface PlaylistActions {
    fun onPlaySong(song: Song)
    fun onPlay()
    fun onShufflePlay()
    fun onDownload()
    fun onBack()
}

@Composable
fun PlaylistScreen(
    playlist: PreparedPlaylist,
    playerViewModel: PlayerViewModel,
    navController: NavController,
) {
    val actions = remember {
        object : PlaylistActions {
            override fun onPlaySong(song: Song) {
                playerViewModel.playImmediate(song)
            }

            override fun onPlay() {
                playerViewModel.playList(playlist)
            }

            override fun onShufflePlay() {
                // TODO
            }

            override fun onDownload() {
                // TODO: Implement download functionality
            }

            override fun onBack() {
                navController.popBackStack()
            }
        }
    }

    PlaylistContent(
        playlist = playlist,
        isUserMade = playlist.provider == Provider.LOCAL,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistContent(
    playlist: PreparedPlaylist,
    isUserMade: Boolean,
    actions: PlaylistActions
) {
    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            IconButton(onClick = actions::onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, null)
            }
        }, title = {}, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212), navigationIconContentColor = Color.White))
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            // Playlist header
            PlaylistHeader(
                playlist = playlist,
                isUserMade = isUserMade,
                onPlayClick = {
                    actions.onPlay()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            ActionButtons(
                onShuffleClick = actions::onShufflePlay,
                onDownloadClick = actions::onDownload
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Songs list
            SongsList(
                songs = playlist.tracks,
                onSongClick = actions::onPlaySong
            )
        }
    }
}

@Composable
private fun PlaylistHeader(
    playlist: PreparedPlaylist,
    isUserMade: Boolean,
    onPlayClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Playlist cover
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFF2A2A2A))
        ) {
            if (playlist.coverUri.isNotEmpty()) {
                AsyncImage(
                    model = playlist.coverUri,
                    contentDescription = "Playlist cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder for playlists without cover
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Artist display
            if (isUserMade) {
                Text(
                    text = "You",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = "Artist Name", // Replace with actual artist name when available
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.clickable {
                        // TODO: Implement artist click action
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${playlist.tracks.size} songs",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        
        // Play button
        IconButton(
            onClick = onPlayClick,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onShuffleClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onShuffleClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = "Shuffle",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Shuffle")
        }
        
        Button(
            onClick = onDownloadClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = "Download",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Download")
        }
    }
}

@Composable
private fun SongsList(songs: List<Song>, onSongClick: (Song) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(songs) { song ->
            SongItem(song = song, onClick = { onSongClick(song) })
        }
    }
}

@Composable
private fun SongItem(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Song thumbnail
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color(0xFF2A2A2A))
        ) {
            if (song.thumbnailUri != null) {
                AsyncImage(
                    model = song.thumbnailUri,
                    contentDescription = "Song thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Text(
            text = formatDuration(song.duration),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

private fun formatDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
private fun PlaylistContentPreview() {
    val mockPlaylist = PreparedPlaylist(
        id = 1,
        title = "My Favorite Songs",
        coverUri = "",
        tracks = listOf(
            Song(
                id = "1",
                title = "Song 1",
                artist = "Artist 1",
                duration = 180000,
                album = "Album 1",
                thumbnailUri = null,
                data = ""
            ),
            Song(
                id = "2",
                title = "Song 2",
                artist = "Artist 2",
                duration = 240000,
                album = "Album 2",
                thumbnailUri = null,
                data = ""
            )
        ),
        provider = Provider.LOCAL
    )

    val mockActions = object : PlaylistActions {
        override fun onPlaySong(song: Song) {
            // Preview only - no action needed
        }

        override fun onShufflePlay() {
            // Preview only - no action needed
        }

        override fun onDownload() {
            // Preview only - no action needed
        }

        override fun onBack() {
            
        }

        override fun onPlay() {

        }
    }

    MusicPlayerTheme {
        PlaylistContent(
            playlist = mockPlaylist,
            isUserMade = true,
            actions = mockActions
        )
    }
}