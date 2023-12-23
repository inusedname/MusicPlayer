package dev.keego.musicplayer.stuff

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PageSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

fun <T: Any> MutableStateFlow<T>.updateTo(update: (T) -> T) {
    value = update(value)
}

@Composable
fun Int.pxToDp(): Dp {
    return with(LocalDensity.current) {
        (this@pxToDp / density).dp
    }
}

object PageSizeUtil {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Fixed(pixel: Int): PageSize {
        return PageSize.Fixed(pixel.pxToDp())
    }
}