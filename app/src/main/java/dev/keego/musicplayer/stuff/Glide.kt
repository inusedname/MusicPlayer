package dev.keego.musicplayer.stuff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object Glide {
    /**
     * @param uri use directly from [UserEntity]'s avatar or must in form of "file:///path/to/image"
     */
    fun ImageView.load(uri: String) {
        Glide.with(this.context).load(uri).into(this)
    }

    /**
     * @param uri use directly from [UserEntity]'s avatar or must in form of "file:///path/to/image"
     * @param clearBitmap must clear every references/usages of the bitmap received in [onComplete]
     */
    fun lazyLoad(
        context: Context,
        uri: String,
        clearBitmap: () -> Unit,
        onComplete: (Bitmap) -> Unit
    ) {
        Glide.with(context).asBitmap().load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onComplete(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    clearBitmap()
                }
            })
    }
}