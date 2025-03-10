package com.eric.Wallpaperadb
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ontvang bestandspad van intent extras
        val filePath = intent.getStringExtra("filePath")

        // Stel de wallpaper in
        setWallpaper(filePath)

        // Sluit de app direct na het instellen van de wallpaper
        finish()
    }

    private fun setWallpaper(filePath: String?) {
        // WallpaperManager verkrijgen
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val originalBitmap: Bitmap? = loadBitmapFromPath(filePath) ?: BitmapFactory.decodeResource(
            resources,
            R.drawable.wallpaper_image
        )

        if (originalBitmap == null) {
            showToast("Fout bij het laden van de afbeelding.")
            return
        }

        // Schermresolutie verkrijgen
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Genereer een geschaalde en gecentreerde bitmap
        val adjustedBitmap = createScaledAndCenteredBitmap(originalBitmap, screenWidth, screenHeight)

        try {
            // Stel de wallpaper in
            wallpaperManager.setBitmap(adjustedBitmap)
            showToast("Wallpaper is ingesteld!")
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Fout bij het instellen van de wallpaper.")
        }
    }

    private fun loadBitmapFromPath(filePath: String?): Bitmap? {
        if (filePath.isNullOrEmpty()) {
            android.util.Log.e("MainActivity", "Geen bestandspad opgegeven")
            return null
        }

        val file = File(filePath)
        if (!file.exists() || !file.canRead()) {
            android.util.Log.e("MainActivity", "Bestand bestaat niet of is niet leesbaar: $filePath")
            return null
        }

        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private fun createScaledAndCenteredBitmap(originalBitmap: Bitmap, screenWidth: Int, screenHeight: Int): Bitmap {
        // Maak een bitmap met de grootte van het scherm
        val resultBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // Schaal de originele bitmap zodat deze in het scherm past
        val scale = max(
            screenWidth.toFloat() / originalBitmap.width,
            screenHeight.toFloat() / originalBitmap.height
        )

        val scaledWidth = (originalBitmap.width * scale).toInt()
        val scaledHeight = (originalBitmap.height * scale).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)

        // Bereken de offsets om de bitmap te centreren
        val offsetX = (screenWidth - scaledWidth) / 2f
        val offsetY = (screenHeight - scaledHeight) / 2f

        // Teken de geschaalde bitmap gecentreerd op het canvas
        canvas.drawBitmap(scaledBitmap, offsetX, offsetY, null)

        return resultBitmap
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}