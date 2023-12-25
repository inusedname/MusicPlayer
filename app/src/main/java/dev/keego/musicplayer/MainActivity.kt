package dev.keego.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.ui.NavGraphs
import dev.keego.musicplayer.ui.destinations.home_Destination
import dev.keego.musicplayer.ui.destinations.search_Destination

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicPlayerTheme {
                val homeNavController = rememberNavController()

                Scaffold(bottomBar = botNav(homeNavController)) {
                    DestinationsNavHost(
                        modifier = Modifier.padding(it),
                        navGraph = NavGraphs.root,
                        navController = homeNavController
                    )
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun botNav(navController: NavController) = @Composable {
        var currentSelect by remember { mutableIntStateOf(0) }
        NavigationBar(containerColor = Color.Transparent, contentColor = Color.White) {
            NavigationBarItem(
                selected = currentSelect == 0,
                onClick = {
                    currentSelect = 0
                    navController.navigate(home_Destination.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(Icons.Outlined.Home, null)
                },
                label = { Text(text = "Home") })
            NavigationBarItem(
                selected = currentSelect == 1,
                onClick = {
                    currentSelect = 1
                    navController.navigate(search_Destination.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(Icons.Outlined.Search, null)
                },
                label = { Text(text = "Search") })
            NavigationBarItem(
                selected = currentSelect == 2,
                onClick = { currentSelect = 2 },
                icon = {
                    Icon(Icons.Outlined.Folder, null)
                },
                label = { Text(text = "Library") })
        }
    }
}