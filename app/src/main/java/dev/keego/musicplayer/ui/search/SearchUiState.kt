package dev.keego.musicplayer.ui.search

import dev.keego.musicplayer.ui.UiState

sealed class SearchUiState {
    data object IDLE : SearchUiState()
    data object LOADING : SearchUiState()
    data class SUCCESS(val result: List<SearchEntry>) : SearchUiState()
    data class ERROR(val throwable: Throwable) : SearchUiState()
}