package dev.ashish.imageapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.ashish.imagecompressor.ImageCompressor

class MainActivity : AppCompatActivity() {
    private lateinit var btnTakePhoto: Button
    private lateinit var btngalleryimage: Button
    private lateinit var takeimage: ImageView
    private var imageUri: Uri? = null
    private val pickedImage = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnTakePhoto = findViewById(R.id.compressImg)
        takeimage = findViewById(R.id.imageView)
        btngalleryimage = findViewById(R.id.btnGelleryPick)

        // Check and request permissions at runtime if needed
        checkPermissions()

        btngalleryimage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickedImage)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickedImage) {
            imageUri = data?.data
            takeimage.setImageURI(imageUri)

            btnTakePhoto.setOnClickListener {
                if (imageUri != null) {
                    val imagePath = imageUri!!.path
                    if (imagePath != null) {
                            // Handle content URI - get the real file path
                            val filePath = getRealPathFromURI(imageUri!!)
                            if (filePath != null) {
                                ImageCompressor.compressImage(filePath,40)
                            }
                    }
                }
            }
        }
    }
    // Function to get real file path from content URI
    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
                // You can proceed with your operations that require this permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}