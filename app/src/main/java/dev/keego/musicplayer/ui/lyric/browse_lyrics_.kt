package dev.keego.musicplayer.ui.lyric

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.remote.lrclib.BestMatchResultPOJO
import dev.keego.musicplayer.stuff.PageSizeUtil
import dev.keego.musicplayer.ui.UiState
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun browse_lyrics_(song: Song, onDismiss: () -> Unit, onSubmit: (BestMatchResultPOJO) -> Unit) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        _browse_lyrics_content(song = song, onDismiss = {
            scope.launch { state.hide(); onDismiss() }
        }, onSubmit = onSubmit)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun _browse_lyrics_content(
    song: Song,
    onDismiss: () -> Unit,
    onSubmit: (BestMatchResultPOJO) -> Unit,
) {
    val vimel = hiltViewModel<BrowseLyricVimel>()
    val uiState by vimel.uiState.collectAsStateWithLifecycle()
    val results by vimel.results.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        vimel.search(song)
    }

    var selectedIdx: Int? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            when (uiState) {
                UiState.LOADING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = "Please wait...", modifier = Modifier.padding(top = 8.dp))
                    }
                }

                is UiState.ERROR -> {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Red
                    )
                    Text(text = (uiState as UiState.ERROR).exception)
                }

                UiState.SUCCESS -> {
                    var viewWidth by remember { mutableIntStateOf(0) }
                    HorizontalPager(
                        state = rememberPagerState { results.size },
                        pageSpacing = 16.dp,
                        contentPadding = PaddingValues(start = 16.dp),
                        pageSize = PageSizeUtil.Fixed(pixel = (viewWidth * 0.8f).toInt()),
                        modifier = Modifier.onSizeChanged { viewWidth = it.width }
                    ) {
                        _result(results[it], selectedIdx == it) {
                            selectedIdx = it
                        }
                    }
                }
            }
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Select your preferred lyric".uppercase(),
            style = MaterialTheme.typography.labelSmall
        )
        Row(Modifier.padding(vertical = 16.dp)) {
            IconButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(0.3f)) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = null
                )
            }
            FilledIconButton(
                onClick = { onSubmit(results[selectedIdx!!]) },
                enabled = selectedIdx != null,
                modifier = Modifier.fillMaxWidth(0.3f),
                colors = IconButtonDefaults.filledIconButtonColors(

                )
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _result(result: BestMatchResultPOJO, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        border = if (selected) BorderStroke(2.dp, Color.White) else null,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)) {
            Text(text = result.trackName, style = MaterialTheme.typography.titleMedium)
            Text(text = result.artistName, style = MaterialTheme.typography.labelSmall)
            Text(
                text = result.plainLyrics,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .weight(1f),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}