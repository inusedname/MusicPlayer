package dev.keego.musicplayer.stuff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

object DiffUtil {
    inline fun <reified T> default(): DiffUtil.ItemCallback<T> {
        return object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem == newItem
            }
        }
    }
}