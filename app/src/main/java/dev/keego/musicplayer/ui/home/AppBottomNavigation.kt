package dev.keego.musicplayer.ui.home

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import dev.keego.musicplayer.Route

@OptIn(UnstableApi::class)
fun AppBottomNavigation(navController: NavController) = @Composable {
    var currentSelect by remember { mutableIntStateOf(0) }
    NavigationBar {
        NavigationBarItem(
            selected = currentSelect == 0,
            onClick = {
                currentSelect = 0
                navController.navigate(Route.Home) {
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
                navController.navigate(Route.Search) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(Icons.Outlined.Search, null)
            },
            label = { Text(text = "Search") })
        NavigationBarItem(
            selected = currentSelect == 2,
            onClick = {
                currentSelect = 2
                navController.navigate(Route.Library) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(Icons.Outlined.Folder, null)
            },
            label = { Text(text = "Library") })
    }
}