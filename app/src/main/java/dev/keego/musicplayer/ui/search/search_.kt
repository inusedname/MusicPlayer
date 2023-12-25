package dev.keego.musicplayer.ui.search

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.pref.AppPreferences
import dev.keego.musicplayer.ui.UiState

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun search_() {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    var showDialogRequestAuth by remember { mutableStateOf(false) }
    val vimel = hiltViewModel<SearchVimel>()
    var query by remember { mutableStateOf("") }
    val resultUiState by vimel.uiState.collectAsStateWithLifecycle()
    val results by vimel.results.collectAsStateWithLifecycle()

    var selectedSongId by remember { mutableStateOf<String?>(null) }

    Column {
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search") },
            trailingIcon = {
                IconButton(onClick = { query = "" }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Search")
                }
            },
            keyboardActions = KeyboardActions(onSearch = {
                keyboard?.hide()
                vimel.query(query)
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        when (resultUiState) {
            SearchUiState.LOADING -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                    )
                    Text(
                        text = "Searching...",
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                }
            }

            SearchUiState.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(results) {
                        _result_entry(it) {
                            selectedSongId = it.deezerId
                            if (AppPreferences.directDownloadToken == null) {
                                showDialogRequestAuth = true
                                return@_result_entry
                            }
                            Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            is SearchUiState.ERROR -> {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${(resultUiState as UiState.ERROR).exception}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            SearchUiState.IDLE -> {
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Search for a song",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showDialogRequestAuth) {
        _auth_required_dialog(
            firstTime = true, // TODO
            onDismiss = { showDialogRequestAuth = false }
        ) {
            showDialogRequestAuth = false
            DirectDownloadAuthActivity.start(context, selectedSongId!!, query)
        }
    }
}

@Composable
private fun _auth_required_dialog(
    firstTime: Boolean,
    onDismiss: () -> Unit,
    onContinue: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Just an extra step")
        },
        text = {
            Text(text = "To automatically download songs from now on, you will have to manually download 1 song first.")
        },
        confirmButton = {
            OutlinedButton(onClick = onContinue) {
                Text(text = "Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}

@Composable
private fun _result_entry(entry: SearchSongEntry, onDownloadClick: () -> Unit) {
    Row(
        Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .clip(Shapes.roundedCornerShape)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = entry.cover,
            contentDescription = null,
            modifier = Modifier
                .clip(Shapes.roundedCornerShape)
                .fillMaxWidth(0.2f)
                .aspectRatio(1f)
        )
        Column(Modifier.padding(start = 24.dp).fillMaxHeight().weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.artist,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .alpha(0.8f)
            )
        }
        FilledIconButton(onClick = onDownloadClick) {
            Icon(imageVector = Icons.Rounded.Download, contentDescription = null)
        }
    }
}
