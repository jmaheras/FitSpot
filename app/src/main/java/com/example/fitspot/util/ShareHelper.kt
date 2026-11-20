package com.example.fitspot.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Opens the Android share sheet with [text] and, optionally, an image.
 *
 * [imageUri] must be a content:// URI that other apps can read (e.g. from FileProvider or
 * MediaStore), not a raw file path.
 *
 * Usage on the Breakdown screen:
 *   shareOutfit(context, "Check out this outfit I found on FitSpot!", imageUri)
 */
fun shareOutfit(context: Context, text: String, imageUri: Uri? = null) {
    val send = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        if (imageUri != null) {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            clipData = ClipData.newRawUri("outfit", imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
        }
    }
    val chooser = Intent.createChooser(send, "Share outfit")
    // Needed when called from a non-Activity context; harmless otherwise.
    if (context !is android.app.Activity) chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}
