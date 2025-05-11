package dev.keego.musicplayer.stuff

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class CustomNavType<T>(
    private val serializer: KSerializer<T>,
) : NavType<T>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let { Json.decodeFromString(serializer, it) }

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putString(key, Json.encodeToString(serializer, value))

    override fun parseValue(value: String): T = Json.decodeFromString(serializer, Uri.decode(value))

    override fun serializeAsValue(value: T): String = Uri.encode(Json.encodeToString(serializer, value))
}