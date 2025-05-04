package dev.keego.musicplayer.ui.search

import dev.keego.musicplayer.remote.Streamable
import org.schabi.newpipe.extractor.InfoItem

data class SearchUiState(
    val searchState: UiState<List<SearchEntry>> = UiState.Idle(),
    val fetchingState: UiState<Streamable> = UiState.Idle()
)

sealed class UiState<T: Any> {
    class Idle<T: Any> : UiState<T>()
    class Loading<T: Any>(val id: Any? = null) : UiState<T>()
    data class Success<T: Any>(val value: T) : UiState<T>()
    data class Error<T: Any>(val exception: Throwable) : UiState<T>()
}