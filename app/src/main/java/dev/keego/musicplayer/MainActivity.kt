package dev.keego.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.keego.musicplayer.databinding.ActivityMainBinding
import dev.keego.musicplayer.home.MusicsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            MusicsFragment()
        ).commitAllowingStateLoss()
    }
}