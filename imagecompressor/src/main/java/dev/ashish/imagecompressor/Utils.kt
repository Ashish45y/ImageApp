package dev.ashish.imagecompressor

import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.IOException

 fun getOutputMediaFile(imageName: String): File? {
    var imageFile1: File? = null
    try {
        imageFile1 = createImageFile(imageName)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    if (imageFile1!!.exists()) imageFile1.delete()
    var imageNew: File? = null
    try {
        imageNew = createImageFile(imageName)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return imageNew
}
 fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    val height = options.outHeight
    val width = options.outWidth

    // Handle potential errors during image dimensions retrieval:
    if (height <= 0 || width <= 0) {
        return 1
    }

    // Calculate the minimum inSampleSize that meets the target dimensions:
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    // Handle potential overflow issues:
    return if (inSampleSize >= Integer.MAX_VALUE / 2) {
        Integer.MAX_VALUE / 2
    } else {
        inSampleSize
    }
}

@Throws(IOException::class)
 fun createImageFile(FileName: String): File {
    return File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + File.separator + FileName + ".png"
    )
}