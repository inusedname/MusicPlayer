import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun MySpaceScreen2() {
    val downloadedSongs = listOf(
        Song("Blinding Lights", "The Weeknd"),
        Song("Save Your Tears", "The Weeknd"),
        Song("Starboy", "The Weeknd ft. Daft Punk")
    )
    val playlists = listOf(
        Playlist("My Favorites", 24),
        Playlist("Workout Mix", 18),
        Playlist("Chill Vibes", 32)
    )
    var offlineMode by remember { mutableStateOf(false) }
    var dataSaver by remember { mutableStateOf(false) }
    var autoDownload by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF18141C))) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar()
            LazyColumn(modifier = Modifier.weight(1f)) {
                item { DownloadedSongsSection(downloadedSongs) }
                item { PlaylistsSection(playlists) }
                item { SettingsSection(offlineMode, { offlineMode = it }, dataSaver, { dataSaver = it }, autoDownload, { autoDownload = it }) }
                item { Spacer(modifier = Modifier.height(80.dp)) } // For bottom bar spacing
            }
        }
        BottomBar()
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("My Space", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_manage),
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun DownloadedSongsSection(songs: List<Song>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = Color(0xFFB18AFF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Downloaded Songs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text("See All", color = Color(0xFFB18AFF), fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        songs.forEach { song ->
            SongRow(song)
            Spacer(modifier = Modifier.height(8.dp))
        }
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB18AFF))
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = Color(0xFFB18AFF),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download More")
        }
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun SongRow(song: Song) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF232029))
        ) {
            // Placeholder for album art
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, color = Color(0xFFB3B3B3), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(android.R.drawable.ic_media_play),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun PlaylistsSection(playlists: List<Playlist>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_sort_by_size),
                contentDescription = null,
                tint = Color(0xFFB18AFF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Your Playlists", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = {}, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                Text("+ New", color = Color(0xFFB18AFF), fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        playlists.forEach { playlist ->
            PlaylistRow(playlist)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun PlaylistRow(playlist: Playlist) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFF232029))
        ) {
            // Placeholder for playlist image
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(playlist.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${playlist.trackCount} tracks", color = Color(0xFFB3B3B3), fontSize = 13.sp)
        }
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(android.R.drawable.ic_media_play),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun SettingsSection(
    offlineMode: Boolean, onOfflineModeChange: (Boolean) -> Unit,
    dataSaver: Boolean, onDataSaverChange: (Boolean) -> Unit,
    autoDownload: Boolean, onAutoDownloadChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_manage),
                contentDescription = null,
                tint = Color(0xFFB18AFF),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        SettingToggle(
            title = "Offline Mode",
            description = "Only play downloaded songs",
            checked = offlineMode,
            onCheckedChange = onOfflineModeChange
        )
        SettingToggle(
            title = "Data Saver",
            description = "Reduce data usage when streaming",
            checked = dataSaver,
            onCheckedChange = onDataSaverChange
        )
        SettingToggle(
            title = "Auto Download",
            description = "Download songs you play often",
            checked = autoDownload,
            onCheckedChange = onAutoDownloadChange
        )
    }
}

@Composable
fun SettingToggle(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(description, color = Color(0xFFB3B3B3), fontSize = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFB18AFF)))
    }
}

@Composable
fun BoxScope.BottomBar() {
    Column(modifier = Modifier.align(Alignment.BottomCenter)) {
        // Mini player
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF232029))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18141C))
            ) {
                // Placeholder for album art
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Blinding Lights", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("The Weeknd", color = Color(0xFFB3B3B3), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_media_play),
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        // Navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF18141C))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem("Discover", selected = false)
            NavBarItem("Search", selected = false)
            NavBarItem("My Space", selected = true)
        }
    }
}

@Composable
fun NavBarItem(label: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_search),
            contentDescription = label,
            tint = if (selected) Color(0xFFB18AFF) else Color(0xFFB3B3B3),
            modifier = Modifier.size(22.dp)
        )
        Text(label, color = if (selected) Color(0xFFB18AFF) else Color(0xFFB3B3B3), fontSize = 12.sp)
    }
}

// Data models

data class Song(val title: String, val artist: String)
data class Playlist(val title: String, val trackCount: Int) 