package dev.ashish.imagecompressor

//noinspection ExifInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
object ImageCompressor {
    suspend fun compressImage(
        imagePath: String,
        imageQuality: Int = 50,
    ) {
        withContext(Dispatchers.IO) {
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageName = "IMG_$timeStamp.jpg"
            val filePath = getOutputMediaFile(imageName)!!.absolutePath

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)

            val reqWidth = 1024
            val reqHeight = 912
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            val bitmap = BitmapFactory.decodeFile(imagePath, options)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true)
            bitmap.recycle()

            val exifOrientation = getExifOrientation(imagePath)
            val rotatedBitmap = rotateBitmap(scaledBitmap, exifOrientation)

            val out = FileOutputStream(filePath)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out)
            out.close()
            rotatedBitmap.recycle()
            filePath
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
