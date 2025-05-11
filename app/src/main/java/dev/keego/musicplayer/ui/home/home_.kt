package dev.keego.musicplayer.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import dev.keego.musicplayer.readLocalAudioPermissionLauncher
import dev.keego.musicplayer.stuff.copySampleAssetsToInternalStorage
import dev.keego.musicplayer.ui.PlayerViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun HomeScreen(navController: NavController, playerViewModel: PlayerViewModel) {
    val vimel = hiltViewModel<HomeVimel>()
    val songs by vimel.songs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = readLocalAudioPermissionLauncher {
        context.copySampleAssetsToInternalStorage()
        vimel.fetch()
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Unit)
    }

    Scaffold(
        topBar = topBar {
            permissionLauncher.launch(Unit)
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(songs) {
                    _song(it) {
                        playerViewModel.playImmediate(
                            it
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
private fun topBar(refresh: () -> Unit) = @Composable {
    TopAppBar(
        title = { Text(text = "All Songs") },
        actions = {
            IconButton(onClick = refresh) {
                Icon(Icons.Rounded.Refresh, null)
            }
        },
    )
}