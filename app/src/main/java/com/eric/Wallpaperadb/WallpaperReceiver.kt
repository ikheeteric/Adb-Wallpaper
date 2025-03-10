package com.eric.Wallpaperadb

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import kotlin.math.max
import android.graphics.Bitmap
import android.graphics.Canvas

class WallpaperReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "com.eric.Wallpaperadb.SET_WALLPAPER") {
            Log.d("WallpaperReceiver", "Set Wallpaper Intent Received")

            val wallpaperManager = WallpaperManager.getInstance(context)
            val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wallpaper_image)

            // Schermresolutie verkrijgen
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            // Genereer een geschaalde en gecentreerde bitmap
            val adjustedBitmap = createScaledAndCenteredBitmap(originalBitmap, screenWidth, screenHeight)

            try {
                wallpaperManager.setBitmap(adjustedBitmap)
                Log.d("WallpaperReceiver", "Wallpaper ingesteld!")
            } catch (e: Exception) {
                Log.e("WallpaperReceiver", "Fout bij het instellen van de wallpaper", e)
            }
        }
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
}
