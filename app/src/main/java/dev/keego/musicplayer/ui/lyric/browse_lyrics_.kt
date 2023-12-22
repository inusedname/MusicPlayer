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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.remote.lrclib.BestMatchResultPOJO
import dev.keego.musicplayer.ui.UiState
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun browse_lyrics_(onDismiss: () -> Unit, onSubmit: (BestMatchResultPOJO) -> Unit) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null
    ) {
        _browse_lyrics_content(onDismiss = {
            scope.launch { state.hide() }
            onDismiss()
        }, onSubmit = onSubmit)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun _browse_lyrics_content(onDismiss: () -> Unit, onSubmit: (BestMatchResultPOJO) -> Unit) {
    val vimel = hiltViewModel<BrowseLyricVimel>()
    val uiState by vimel.uiState.collectAsStateWithLifecycle()
    val results by vimel.results.collectAsStateWithLifecycle()

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
                    CircularProgressIndicator()
                    Text(text = "Please wait...")
                }

                is UiState.ERROR -> {
                    Text(text = "Error", fontWeight = FontWeight.Bold, color = Color.Red)
                    Text(text = (uiState as UiState.ERROR).exception)
                }

                UiState.SUCCESS -> {
                    HorizontalPager(state = rememberPagerState(
                        initialPage = 0,
                        initialPageOffsetFraction = 0.2f
                    ) { results.size }) {
                        _result(results[it], selectedIdx == it) {
                            selectedIdx = it
                        }
                    }
                }
            }
        }
        Text(text = "Select your preferred lyric".uppercase(), style = MaterialTheme.typography.labelSmall)
        IconButton(onClick = onDismiss) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.2f)
            )
        }
        IconButton(onClick = { onSubmit(results[selectedIdx!!]) }, enabled = selectedIdx != null) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxWidth(0.2f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _result(result: BestMatchResultPOJO, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = Shapes.roundedCornerShape,
        border = if (selected) null else BorderStroke(2.dp, Color.White),
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(text = result.trackName, fontWeight = FontWeight.Bold)
            Text(text = result.artistName)
            Text(
                text = result.plainLyrics,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}