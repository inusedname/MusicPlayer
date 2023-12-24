package dev.keego.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.ui.NavGraphs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicPlayerTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}