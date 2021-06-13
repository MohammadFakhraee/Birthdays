package ir.mohammadhf.birthdays.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

class ImageLoader @Inject constructor() {

    fun load(context: Context, imagePath: String, imageView: ImageView) =
        Glide.with(context)
            .load(Uri.fromFile(File(imagePath)))
            .into(imageView)
}