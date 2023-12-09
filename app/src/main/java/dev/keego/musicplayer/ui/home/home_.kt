package dev.keego.musicplayer.ui.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.MediaPermission
import dev.keego.musicplayer.stuff.copySampleAssetsToInternalStorage
import dev.keego.musicplayer.ui.home.destinations.home_Destination.style

@RootNavGraph(start = true)
@Destination
@Composable
fun home_(navigator: DestinationsNavigator) {
    val activity = LocalContext.current as Activity
    val vimel = hiltViewModel<HomeVimel>()
    val songs by vimel.songs.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        MediaPermission
            .onGranted {
                activity.copySampleAssetsToInternalStorage()
                vimel.fetch()
            }
            .request(activity)
    }

    Scaffold(topBar = topBar(), bottomBar = botNav(navigator)) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(songs) {
                    _song(it) {

                    }
                }
            }
        }
    }
}

@Composable
fun _song(song: Song, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(Shapes.roundedCornerShape)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = song.albumUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.2f)
                .aspectRatio(1f)
                .clip(Shapes.roundedCornerShape)
        )
        Column(Modifier.padding(start = 24.dp)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist ?: "Unknown",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .alpha(0.8f)
            )
        }
    }
}

fun botNav(navigator: DestinationsNavigator) = @Composable {
    var currentSelect by remember { mutableIntStateOf(0) }
    NavigationBar {
        NavigationBarItem(selected = currentSelect == 0, onClick = { currentSelect = 0 }, icon = {
            Icon(Icons.Outlined.Home, null)
        }, label = { Text(text = "Home") })
        NavigationBarItem(selected = currentSelect == 1, onClick = { currentSelect = 1 }, icon = {
            Icon(Icons.Outlined.Search, null)
        }, label = { Text(text = "Explore") })
        NavigationBarItem(selected = currentSelect == 2, onClick = { currentSelect = 2 }, icon = {
            Icon(Icons.Outlined.Folder, null)
        }, label = { Text(text = "Library") })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun topBar() = @Composable {
    TopAppBar(
        title = { Text(text = "All Songs") },
        navigationIcon = { IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Rounded.ArrowBack, null)
        } }
    )
}