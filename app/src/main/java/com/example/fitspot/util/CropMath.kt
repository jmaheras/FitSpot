package com.example.fitspot.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Which part of the crop box the user is dragging. */
enum class CropHandle { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, BODY }

/** Where an image is drawn inside a container (letterboxed to preserve aspect ratio). */
data class FitRect(val left: Float, val top: Float, val width: Float, val height: Float)

/** Crop region in the image's own pixels. */
data class PixelRect(val x: Int, val y: Int, val width: Int, val height: Int)

/**
 * Crop box in normalized image coordinates (0..1 on both axes), so it is independent of the
 * screen size and the bitmap resolution.
 */
data class CropRect(
    val left: Float = 0f,
    val top: Float = 0f,
    val right: Float = 1f,
    val bottom: Float = 1f
) {
    val width get() = right - left
    val height get() = bottom - top

    /**
     * Returns this box after dragging [handle] by ([dx], [dy]), also in normalized units.
     * The box stays inside 0..1 and never shrinks below [minSize] on either axis.
     */
    fun dragged(handle: CropHandle, dx: Float, dy: Float, minSize: Float = 0.1f): CropRect =
        when (handle) {
            CropHandle.BODY -> {
                val nx = (left + dx).coerceIn(0f, 1f - width)
                val ny = (top + dy).coerceIn(0f, 1f - height)
                CropRect(nx, ny, nx + width, ny + height)
            }
            CropHandle.TOP_LEFT -> copy(
                left = (left + dx).coerceIn(0f, right - minSize),
                top = (top + dy).coerceIn(0f, bottom - minSize)
            )
            CropHandle.TOP_RIGHT -> copy(
                right = (right + dx).coerceIn(left + minSize, 1f),
                top = (top + dy).coerceIn(0f, bottom - minSize)
            )
            CropHandle.BOTTOM_LEFT -> copy(
                left = (left + dx).coerceIn(0f, right - minSize),
                bottom = (bottom + dy).coerceIn(top + minSize, 1f)
            )
            CropHandle.BOTTOM_RIGHT -> copy(
                right = (right + dx).coerceIn(left + minSize, 1f),
                bottom = (bottom + dy).coerceIn(top + minSize, 1f)
            )
        }

    /** Converts this box to pixels of an image that is [imageWidth] x [imageHeight]. */
    fun toPixelRect(imageWidth: Int, imageHeight: Int): PixelRect {
        val x = (left * imageWidth).roundToInt().coerceIn(0, imageWidth - 1)
        val y = (top * imageHeight).roundToInt().coerceIn(0, imageHeight - 1)
        val w = (width * imageWidth).roundToInt().coerceIn(1, imageWidth - x)
        val h = (height * imageHeight).roundToInt().coerceIn(1, imageHeight - y)
        return PixelRect(x, y, w, h)
    }
}

/** Largest centered rectangle of an image with the given size that fits in the container. */
fun fitRect(containerW: Float, containerH: Float, imageW: Int, imageH: Int): FitRect {
    val scale = min(containerW / imageW, containerH / imageH)
    val w = imageW * scale
    val h = imageH * scale
    return FitRect((containerW - w) / 2f, (containerH - h) / 2f, max(w, 1f), max(h, 1f))
}
