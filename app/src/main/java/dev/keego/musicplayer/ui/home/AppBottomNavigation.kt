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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.keego.musicplayer.Route
import kotlin.reflect.KClass

@OptIn(UnstableApi::class)
fun AppBottomNavigation(navController: NavController) = @Composable {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currDestination = currentBackStackEntry?.destination

    fun NavDestination?.match(route: KClass<*>) = this?.hierarchy?.any { it.hasRoute(route)} ?: false

    data class TopRoute(val title: String, val route: Route, val icon: ImageVector)

    val routes = listOf(
        TopRoute("Home", Route.Home, Icons.Outlined.Home),
        TopRoute("Search", Route.Search, Icons.Outlined.Search),
        TopRoute("Library", Route.Library, Icons.Outlined.Folder)
    )

    NavigationBar {
        routes.map {
            NavigationBarItem(
                selected = currDestination.match(it.route::class),
                onClick = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(Route.Home)
                    }
                },
                icon = {
                    Icon(it.icon, null)
                },
                label = { Text(text = it.title) }
            )
        }
    }
}