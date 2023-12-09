package dev.keego.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import dev.keego.musicplayer.config.theme.MusicPlayerTheme

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                body_()
            }
        }
    }
}

@Composable
fun body_() {
    Row {

    }
}