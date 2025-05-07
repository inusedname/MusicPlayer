package dev.keego.musicplayer.ui.setting

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download

/**
 * TODO: Nghiệp vụ:
 * - Ở search, ấn download thì sẽ thêm id vào queue của download service. sau đó thì gọi post cho từng cái để lấy link
 * - Ở setting: show downloaded + pause download, stop các thứ
 */
@Composable
fun setting_() {
    Scaffold {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            _download {

            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun _download(onClick: () -> Unit) {
    val settingVimel = hiltViewModel<SettingVimel>()
    val downloads by settingVimel.downloads.collectAsStateWithLifecycle()

    val songDownloaded = remember(downloads) {
//        downloads.count { it.state == Download.STATE_COMPLETED }
        0
    }
    val overallProgress = 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
        , onClick = onClick
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = "Downloads", style = MaterialTheme.typography.titleMedium)
            when {
                downloads.isEmpty() -> {
                    Text(text = "No downloads in queue 🎉~", style = MaterialTheme.typography.headlineSmall)
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
}

