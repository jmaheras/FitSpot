package com.example.fitspot.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.fitspot.util.CropHandle
import com.example.fitspot.util.CropRect
import com.example.fitspot.util.cropped
import com.example.fitspot.util.fitRect

/**
 * Lets the user drag the corners (or the body) of a box over [bitmap] to isolate part of an
 * outfit.
 *
 *  - "Crop"        -> [onCropped] with just the selected region
 *  - "Don't Crop"  -> [onSkip] (the caller keeps using the full image)
 *  - "Retake"      -> [onRetake], shown only if non-null
 *
 * Intended to sit between photo capture/selection and the scan step:
 *   CropScreen(bitmap, onCropped = { scan(it) }, onSkip = { scan(bitmap) })
 */
@Composable
fun CropScreen(
    bitmap: Bitmap,
    onCropped: (Bitmap) -> Unit,
    onSkip: () -> Unit,
    onRetake: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var rect by remember(bitmap) { mutableStateOf(CropRect()) }
    val image = remember(bitmap) { bitmap.asImageBitmap() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Crop", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Drag the corners to select a clothing item, or scan the whole outfit.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(bitmap) {
                    val hitRadius = 36.dp.toPx()
                    var active: CropHandle? = null

                    detectDragGestures(
                        onDragStart = { start ->
                            val fit = fitRect(size.width.toFloat(), size.height.toFloat(), bitmap.width, bitmap.height)
                            fun cx(n: Float) = fit.left + n * fit.width
                            fun cy(n: Float) = fit.top + n * fit.height
                            val corners = listOf(
                                CropHandle.TOP_LEFT to Offset(cx(rect.left), cy(rect.top)),
                                CropHandle.TOP_RIGHT to Offset(cx(rect.right), cy(rect.top)),
                                CropHandle.BOTTOM_LEFT to Offset(cx(rect.left), cy(rect.bottom)),
                                CropHandle.BOTTOM_RIGHT to Offset(cx(rect.right), cy(rect.bottom))
                            )
                            val nearest = corners.minByOrNull { (_, p) -> (p - start).getDistance() }
                            active = when {
                                nearest != null && (nearest.second - start).getDistance() <= hitRadius -> nearest.first
                                start.x in cx(rect.left)..cx(rect.right) &&
                                    start.y in cy(rect.top)..cy(rect.bottom) -> CropHandle.BODY
                                else -> null
                            }
                        },
                        onDrag = { change, drag ->
                            val handle = active ?: return@detectDragGestures
                            change.consume()
                            val fit = fitRect(size.width.toFloat(), size.height.toFloat(), bitmap.width, bitmap.height)
                            rect = rect.dragged(handle, drag.x / fit.width, drag.y / fit.height)
                        },
                        onDragEnd = { active = null },
                        onDragCancel = { active = null }
                    )
                }
        ) {
            val fit = fitRect(size.width, size.height, bitmap.width, bitmap.height)
            drawImage(
                image,
                dstOffset = IntOffset(fit.left.toInt(), fit.top.toInt()),
                dstSize = IntSize(fit.width.toInt(), fit.height.toInt())
            )

            val l = fit.left + rect.left * fit.width
            val t = fit.top + rect.top * fit.height
            val r = fit.left + rect.right * fit.width
            val b = fit.top + rect.bottom * fit.height

            // Dim everything outside the crop box (inside the image area)
            val dim = Color.Black.copy(alpha = 0.55f)
            drawRect(dim, Offset(fit.left, fit.top), Size(fit.width, t - fit.top))
            drawRect(dim, Offset(fit.left, b), Size(fit.width, fit.top + fit.height - b))
            drawRect(dim, Offset(fit.left, t), Size(l - fit.left, b - t))
            drawRect(dim, Offset(r, t), Size(fit.left + fit.width - r, b - t))

            drawRect(Color.White, Offset(l, t), Size(r - l, b - t), style = Stroke(width = 3.dp.toPx()))
            listOf(Offset(l, t), Offset(r, t), Offset(l, b), Offset(r, b)).forEach {
                drawCircle(Color.White, radius = 11.dp.toPx(), center = it)
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("Don't Crop") }
            Button(onClick = { onCropped(bitmap.cropped(rect)) }, modifier = Modifier.weight(1f)) {
                Text("Crop")
            }
        }
        onRetake?.let {
            TextButton(onClick = it, modifier = Modifier.fillMaxWidth()) { Text("Retake") }
        }
    }
}
