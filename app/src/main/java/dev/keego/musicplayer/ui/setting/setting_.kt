package dev.keego.musicplayer.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import com.ramcosta.composedestinations.annotation.Destination

/**
 * TODO: Nghiệp vụ:
 * - Ở search, ấn download thì sẽ thêm id vào queue của download service. sau đó thì gọi post cho từng cái để lấy link
 * - Ở setting: show downloaded + pause download, stop các thứ
 */
@UnstableApi
@Destination
@Composable
fun setting_() {
    Scaffold {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            _download {

            }
        }
    }
}

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _download(onClick: () -> Unit) {
    val settingVimel = hiltViewModel<SettingVimel>()
    val downloads by settingVimel.downloads.collectAsStateWithLifecycle()

    val songDownloaded = remember(downloads) {
        downloads.count { it.state == Download.STATE_COMPLETED }
    }
    val overallProgress = 0.5f

    Card(modifier = Modifier.padding(8.dp), onClick = onClick) {
        Text(text = "Downloads")
        when {
            downloads.isEmpty() -> {
                Text(text = "No downloads in queue 🎉~")
                Text(text = "Song downloaded: $songDownloaded")
            }

            else -> {
                Text(text = "Downloading: ${downloads[0]}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${overallProgress * 100}%",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    LinearProgressIndicator(
                        progress = overallProgress,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

