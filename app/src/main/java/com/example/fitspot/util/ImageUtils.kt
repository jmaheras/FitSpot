package com.example.fitspot.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream

/**
 * Loads an image from a camera or gallery [uri], downsampled so its longest side is at most
 * [maxSide] pixels, and rotated upright according to its EXIF orientation.
 * Returns null if the image can't be read.
 */
fun loadBitmap(context: Context, uri: Uri, maxSide: Int = 2048): Bitmap? {
    val resolver = context.contentResolver
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: return null

        val orientation = resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return decoded
        }
        Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
    } catch (e: Exception) {
        null
    }
}

private fun max(a: Int, b: Int) = if (a > b) a else b

/** Returns the part of this bitmap inside [rect] as a new bitmap. */
fun Bitmap.cropped(rect: CropRect): Bitmap {
    val p = rect.toPixelRect(width, height)
    return Bitmap.createBitmap(this, p.x, p.y, p.width, p.height)
}

/** JPEG-encodes this bitmap, e.g. to base64 it for an image-recognition API request. */
fun Bitmap.toJpegBytes(quality: Int = 90): ByteArray =
    ByteArrayOutputStream().also { compress(Bitmap.CompressFormat.JPEG, quality, it) }.toByteArray()
