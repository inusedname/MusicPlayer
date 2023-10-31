package dev.keego.musicplayer.home

import android.Manifest
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import dev.keego.musicplayer.databinding.FragmentMusicsBinding
import dev.keego.musicplayer.stuff.BaseFragment
import dev.keego.musicplayer.stuff.MediaQuery
import dev.keego.musicplayer.stuff.PermissionCenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicsFragment : BaseFragment<FragmentMusicsBinding>() {
    private val permissionary = PermissionCenter(this)

    override fun onViewCreated(binding: FragmentMusicsBinding) {
        requestPermission()
        setupView(binding)
    }

    private fun setupView(binding: FragmentMusicsBinding) {
        binding.rvMusics.adapter = MusicsAdapter {
            toast("Clicked: ${it.albumUri}")
        }
    }

    private fun requestPermission() {
        permissionary
            .permissions(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_AUDIO
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onPermissionAllGranted {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    context?.let { MediaQuery.querySongs(it) }
                }
            }
            .launch()
    }

    override fun constructViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMusicsBinding {
        return FragmentMusicsBinding.inflate(inflater, container, false)
    }
}