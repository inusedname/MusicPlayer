package dev.keego.musicplayer.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import dev.keego.musicplayer.domain.PreparedPlaylist
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PreparedPlaylistNavType : NavType<PreparedPlaylist>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PreparedPlaylist? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): PreparedPlaylist {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: PreparedPlaylist) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: PreparedPlaylist): String {
        return Uri.encode(Json.encodeToString(value))
    }
} 