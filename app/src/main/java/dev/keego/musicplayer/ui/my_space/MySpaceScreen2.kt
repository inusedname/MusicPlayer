import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.keego.musicplayer.Route
import dev.keego.musicplayer.domain.PreparedPlaylist
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.ui.MockData
import dev.keego.musicplayer.ui.my_space.MySpaceViewModel

@Composable
fun MySpaceScreen(navController: NavController) {
    val viewModel = hiltViewModel<MySpaceViewModel>()
    val playlists by viewModel.playlists.collectAsState()

    val downloadedSongs = listOf<Song>(
        Song(id = "1", album = "", "Hello", 100, "Adele", "", data = "")
    )
    var offlineMode by remember { mutableStateOf(false) }
    var dataSaver by remember { mutableStateOf(false) }
    var autoDownload by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { TopBar {
            navController.navigate(Route.Setting)
        } },
        containerColor = Color(0xFF121212),
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item { DownloadedSongsSection(downloadedSongs) }
                item { PlaylistsSection(playlists) {
                    navController.navigate(Route.Playlist(it))
                } }
                item { SettingsSection(offlineMode, { offlineMode = it }, dataSaver, { dataSaver = it }, autoDownload, { autoDownload = it }) }
                item { Spacer(modifier = Modifier.height(80.dp)) } // For bottom bar spacing
            }
        }
    }
}

@Composable
private fun TopBar(onSettingClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("My Space", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onSettingClick) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_manage),
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DownloadedSongsSection(songs: List<Song>) {
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
private fun SongRow(song: Song) {
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
private fun PlaylistsSection(playlists: List<PreparedPlaylist>, onClick: (PreparedPlaylist) -> Unit) {
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
            PlaylistRow(playlist) { onClick(playlist) }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun PlaylistRow(playlist: PreparedPlaylist, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
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
            Text("${playlist.tracks.size} tracks", color = Color(0xFFB3B3B3), fontSize = 13.sp)
        }
    }
}

@Composable
private fun SettingsSection(
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
private fun SettingToggle(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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