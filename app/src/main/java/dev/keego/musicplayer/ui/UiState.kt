package dev.keego.musicplayer.ui

sealed class UiState {
    data object LOADING : UiState()
    data object SUCCESS : UiState()
    data class ERROR(val exception: String) : UiState()
}