package com.example.fitspot

import com.example.fitspot.sensors.ShakeDetector
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeDetectorTest {
    private val g = 9.81f

    @Test
    fun resting_device_is_not_a_shake() {
        val d = ShakeDetector()
        assertFalse(d.onSample(0f, 0f, g, 0L))
    }

    @Test
    fun strong_motion_is_a_shake() {
        val d = ShakeDetector(thresholdG = 2.7f)
        assertTrue(d.onSample(3f * g, 0f, 0f, 0L))
    }

    @Test
    fun repeated_samples_within_cooldown_report_once() {
        val d = ShakeDetector(thresholdG = 2.7f, cooldownMs = 1000L)
        assertTrue(d.onSample(3f * g, 0f, 0f, 0L))
        assertFalse(d.onSample(3f * g, 0f, 0f, 500L))
        assertTrue(d.onSample(3f * g, 0f, 0f, 1500L))
    }

    @Test
    fun shake_at_time_zero_does_not_block_later_shakes_incorrectly() {
        val d = ShakeDetector(cooldownMs = 1000L)
        assertTrue(d.onSample(4f * g, 0f, 0f, 0L))
        assertTrue(d.onSample(4f * g, 0f, 0f, 1000L))
    }
}
