package dev.keego.musicplayer.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import coil.compose.AsyncImage
import dev.keego.musicplayer.config.theme.Shapes
import dev.keego.musicplayer.remote.Streamable
import dev.keego.musicplayer.ui.PlayerViewModel

@Composable
fun SearchScreen(playerViewModel: PlayerViewModel) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current

    val viewModel = hiltViewModel<SearchVimel>()
    val resultUiState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }

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
                viewModel.query(query)
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

        LaunchedEffect(Unit) {
            viewModel.state.collect {
                if (it.fetchingState is UiState.Success) {
                    playerViewModel.playImmediate(it.fetchingState.value, false)
                    viewModel.markAsPlayed()
                }
            }
        }

        when (val state = resultUiState.searchState) {
            is UiState.Loading -> {
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

            is UiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.value) { entry ->
                        _result_entry(
                            entry = entry,
                            showLoading = resultUiState.fetchingState is UiState.Loading && (resultUiState.fetchingState as UiState.Loading<Streamable>).id == entry.detail.url,
                            onItemClick = {
                                viewModel.getStreamable(entry)
                            }
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.exception.message}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is UiState.Idle -> {
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
}

@Composable
private fun _result_entry(
    entry: SearchEntry,
    showLoading: Boolean,
    onItemClick: () -> Unit
) {
    Row(
        Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onItemClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = entry.detail.thumbnails.firstOrNull()?.url,
            contentDescription = null,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth(0.2f)
                .aspectRatio(1f)
        )
        Column(
            Modifier
                .padding(start = 24.dp)
                .fillMaxHeight()
                .weight(1f)
        ) {
            Text(
                text = entry.detail.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "No name",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .alpha(0.8f)
            )
        }
        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
