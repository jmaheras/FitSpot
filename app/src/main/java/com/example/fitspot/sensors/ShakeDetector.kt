package com.example.fitspot.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

/**
 * Pure shake-detection logic (no Android types) so it can be unit tested.
 * A shake is an accelerometer reading whose magnitude exceeds [thresholdG] times gravity,
 * with at least [cooldownMs] between reported shakes.
 */
class ShakeDetector(
    private val thresholdG: Float = 2.7f,
    private val cooldownMs: Long = 1000L
) {
    private var lastShakeMs: Long = Long.MIN_VALUE

    /** Feed one accelerometer sample (m/s^2). Returns true if this sample counts as a new shake. */
    fun onSample(x: Float, y: Float, z: Float, timeMs: Long): Boolean {
        val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
        if (gForce < thresholdG) return false
        if (lastShakeMs != Long.MIN_VALUE && timeMs - lastShakeMs < cooldownMs) return false
        lastShakeMs = timeMs
        return true
    }
}

/**
 * Calls [onShake] whenever the device is shaken while [enabled] is true.
 * Listens to the accelerometer only while this composable is in the composition.
 *
 * Usage on the scan screen:
 *   ShakeToRetake(enabled = capturedImage != null) { capturedImage = null }
 */
@Composable
fun ShakeToRetake(enabled: Boolean = true, onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(enabled) {
        if (!enabled) return@DisposableEffect onDispose { }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensorManager == null || accelerometer == null) {
            return@DisposableEffect onDispose { } // no accelerometer: feature silently off
        }

        val detector = ShakeDetector()
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val (x, y, z) = event.values
                if (detector.onSample(x, y, z, System.currentTimeMillis())) currentOnShake()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose { sensorManager.unregisterListener(listener) }
    }
}
