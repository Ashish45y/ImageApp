package dev.ashish.imagecompressor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
//noinspection ExifInterface
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
object ImageCompressor {
    fun compressImage(imagePath: String, imageQuality: Int = 50): String {
        val file = File(imagePath)
        if (!file.exists()) {
            return ""
        }

        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageName = "IMG_$timeStamp.jpg"

        val filePath = getOutputMediaFile(imageName)!!.absolutePath

        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)

            val reqWidth = 1024
            val reqHeight = 912

            val actualWidth = options.outWidth
            val actualHeight = options.outHeight

            // Calculate inSampleSize for downscaling
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            options.inJustDecodeBounds = false
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            val bitmap = BitmapFactory.decodeFile(imagePath, options)
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap, reqWidth, reqHeight, true
            )
            bitmap.recycle() // Release memory used by the original bitmap

            val exifOrientation = getExifOrientation(imagePath)
            val rotatedBitmap = rotateBitmap(scaledBitmap, exifOrientation)

            val out = FileOutputStream(filePath)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out)
            out.close()
            rotatedBitmap.recycle() // Release memory used by the scaled bitmap

            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private fun getExifOrientation(imagePath: String): Int {
        return try {
            val exif = ExifInterface(imagePath)
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        } catch (e: IOException) {
            Log.e("ImageCompressor", "Error getting exif orientation: $e")
            0
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            6 -> matrix.postRotate(90f)
            3 -> matrix.postRotate(180f)
            8 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }
}
