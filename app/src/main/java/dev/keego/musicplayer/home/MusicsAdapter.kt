package dev.keego.musicplayer.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keego.musicplayer.databinding.ItemMusicBinding
import dev.keego.musicplayer.model.Song
import dev.keego.musicplayer.stuff.DiffUtil
import dev.keego.musicplayer.stuff.Glide.load

class MusicsAdapter(private val onClick: (Song) -> Unit) :
    ListAdapter<Song, MusicsAdapter.ViewHolder>(DiffUtil.default()) {
    class ViewHolder(private val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.apply {
                cover.load(song.albumUri)
                title.text = song.title
                artistLength.text = String.format("%s - %s", song.artist, song.duration)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}