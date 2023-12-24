package dev.keego.musicplayer.ui.search

import dev.keego.musicplayer.ui.UiState

sealed class SearchUiState {
    data object IDLE : SearchUiState()
    data object LOADING : SearchUiState()
    data object SUCCESS : SearchUiState()
    data class ERROR(val exception: String) : SearchUiState()
}