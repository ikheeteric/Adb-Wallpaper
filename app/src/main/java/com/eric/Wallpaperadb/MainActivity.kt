package com.eric.Wallpaperadb

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkStoragePermission()) {
            processWallpaper()
        } else {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_CODE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processWallpaper()
            } else {
                showToast("Toegang tot opslag is geweigerd. Kan geen wallpaper instellen.")
                finish()
            }
        }
    }

    private fun processWallpaper() {
        val filePath = intent.getStringExtra("filePath")

        if (filePath.isNullOrEmpty()) {
            showToast("Fout: Geen geldig bestandspad opgegeven.")
            finish()
            return
        }

        setWallpaper(filePath)
        finish()
    }

    private fun setWallpaper(filePath: String) {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val originalBitmap = loadBitmapFromPath(filePath)

        if (originalBitmap == null) {
            showToast("Fout bij het laden van de afbeelding. Controleer het bestandspad.")
            return
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val adjustedBitmap = createScaledAndCenteredBitmap(originalBitmap, screenWidth, screenHeight)

        try {
            wallpaperManager.setBitmap(adjustedBitmap)
            showToast("Wallpaper succesvol ingesteld!")
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Fout bij het instellen van de wallpaper.")
        }
    }

    private fun loadBitmapFromPath(filePath: String): Bitmap? {
        val file = File(filePath)
        if (!file.exists() || !file.canRead()) {
            android.util.Log.e("MainActivity", "Bestand bestaat niet of is niet leesbaar: $filePath")
            return null
        }
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private fun createScaledAndCenteredBitmap(originalBitmap: Bitmap, screenWidth: Int, screenHeight: Int): Bitmap {
        val resultBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        val scale = max(
            screenWidth.toFloat() / originalBitmap.width,
            screenHeight.toFloat() / originalBitmap.height
        )

        val scaledWidth = (originalBitmap.width * scale).toInt()
        val scaledHeight = (originalBitmap.height * scale).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)

        val offsetX = (screenWidth - scaledWidth) / 2f
        val offsetY = (screenHeight - scaledHeight) / 2f

        canvas.drawBitmap(scaledBitmap, offsetX, offsetY, null)

        return resultBitmap
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
