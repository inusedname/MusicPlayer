package dev.keego.musicplayer.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vstd.base_sdk_view.base.utils.toast
import dev.keego.musicplayer.ui.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(playerViewModel: PlayerViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val viewModel = hiltViewModel<SearchVimel>()
    val resultUiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    fun onItemClick(item: SearchEntry) {
        playerViewModel.playImmediate(item)
    }

    fun handleSearch(searchQuery: String) {
        viewModel.search(searchQuery)
    }

    fun addToPlayNext(item: SearchEntry) {
        context.toast("Added to play next!")
        playerViewModel.playNext(item)
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Search")
        })
    }) { paddingValues ->
        Column(Modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
            MySearchBar(searchQuery, onQueryChange = {
                searchQuery = it
            }, viewModel, ::handleSearch)

            Spacer(modifier = Modifier.height(12.dp))

            if (resultUiState.searchState is UiState.Success && searchQuery.isNotEmpty()) {
                when (val searchState = resultUiState.searchState) {
                    is UiState.Loading -> {
                        // Show loading indicator
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Success -> {
                        // Show search results
                        SearchResultsList(searchState.value, ::onItemClick, ::addToPlayNext)
                    }

                    is UiState.Error -> {
                        // Show error message
                        Text(
                            text = "Error: ${searchState.exception.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is UiState.Idle -> {}
                }
            } else if (searchQuery.isEmpty()) {
                // Search history when not actively searching
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    LazyColumn {
                        items(resultUiState.searchHistories) { query ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchQuery = query
                                        handleSearch(query)
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = query,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { /* Remove from history */ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MySearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    viewModel: SearchVimel,
    handleSearch: (String) -> Unit,
) {
    var active by remember { mutableStateOf(false) }
    val resultUiState by viewModel.state.collectAsState()
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = {
                    onQueryChange(it)
                    viewModel.getSuggestions(it)
                },
                onSearch = {
                    handleSearch(searchQuery)
                    active = false
                },
                placeholder = { Text("Search for songs, artists, or playlists") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onQueryChange("")
                            viewModel.getSuggestions("")
                        }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                expanded = active,
                onExpandedChange = { active = it },
            )
        },
        modifier = Modifier.fillMaxWidth(),
        expanded = active,
        onExpandedChange = { active = it },
    ) {
        if (searchQuery.isNotEmpty()) {
            // Generate suggestions based on the query
            resultUiState.suggestions.map { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            active = false
                            onQueryChange(suggestion)
                            handleSearch(searchQuery)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(suggestion)
                }
            }
        } else {
            // Show recent searches when no query
            resultUiState.searchHistories.forEach { query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            active = false
                            onQueryChange(query)
                            handleSearch(query)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(query)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResultsList(results: List<SearchEntry>, onItemClick: (SearchEntry) -> Unit, onLongClick: (SearchEntry) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(results) { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onItemClick(result) },
                        onLongClick = { onLongClick(result) }
                    )
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = result.thumbnailUrl,
                    contentDescription = result.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = result.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = formatDuration(result.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
}

