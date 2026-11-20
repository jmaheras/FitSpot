package com.example.fitspot

import com.example.fitspot.util.CropHandle
import com.example.fitspot.util.CropRect
import com.example.fitspot.util.PixelRect
import com.example.fitspot.util.fitRect
import org.junit.Assert.assertEquals
import org.junit.Test

class CropMathTest {
    private val eps = 1e-4f

    @Test
    fun default_box_covers_whole_image() {
        assertEquals(PixelRect(0, 0, 400, 300), CropRect().toPixelRect(400, 300))
    }

    @Test
    fun dragging_top_left_inward_shrinks_box() {
        val r = CropRect().dragged(CropHandle.TOP_LEFT, 0.25f, 0.5f)
        assertEquals(0.25f, r.left, eps)
        assertEquals(0.5f, r.top, eps)
        assertEquals(1f, r.right, eps)
        assertEquals(1f, r.bottom, eps)
    }

    @Test
    fun corners_cannot_leave_the_image() {
        val r = CropRect().dragged(CropHandle.TOP_LEFT, -0.5f, -0.5f)
        assertEquals(0f, r.left, eps)
        assertEquals(0f, r.top, eps)
        val r2 = CropRect().dragged(CropHandle.BOTTOM_RIGHT, 0.5f, 0.5f)
        assertEquals(1f, r2.right, eps)
        assertEquals(1f, r2.bottom, eps)
    }

    @Test
    fun box_cannot_shrink_below_min_size() {
        val r = CropRect().dragged(CropHandle.TOP_LEFT, 5f, 5f, minSize = 0.1f)
        assertEquals(0.9f, r.left, eps)
        assertEquals(0.9f, r.top, eps)
        assertEquals(0.1f, r.width, eps)
    }

    @Test
    fun dragging_opposite_corners_never_crosses_over() {
        val r = CropRect(0.2f, 0.2f, 0.6f, 0.6f).dragged(CropHandle.BOTTOM_RIGHT, -1f, -1f, minSize = 0.1f)
        assertEquals(0.3f, r.right, eps)
        assertEquals(0.3f, r.bottom, eps)
    }

    @Test
    fun body_drag_moves_box_and_clamps_to_edges() {
        val start = CropRect(0.2f, 0.2f, 0.6f, 0.5f)
        val moved = start.dragged(CropHandle.BODY, 0.1f, 0.1f)
        assertEquals(0.3f, moved.left, eps)
        assertEquals(0.7f, moved.right, eps)
        assertEquals(start.width, moved.width, eps)
        val clamped = start.dragged(CropHandle.BODY, 9f, 9f)
        assertEquals(1f, clamped.right, eps)
        assertEquals(1f, clamped.bottom, eps)
        assertEquals(start.width, clamped.width, eps)
        assertEquals(start.height, clamped.height, eps)
    }

    @Test
    fun pixel_rect_scales_to_image_size() {
        val p = CropRect(0.25f, 0.5f, 0.75f, 1f).toPixelRect(400, 200)
        assertEquals(PixelRect(100, 100, 200, 100), p)
    }

    @Test
    fun pixel_rect_stays_inside_image() {
        val p = CropRect(0.999f, 0.999f, 1f, 1f).toPixelRect(100, 100)
        assertEquals(true, p.x + p.width <= 100 && p.y + p.height <= 100 && p.width >= 1 && p.height >= 1)
    }

    @Test
    fun fit_rect_letterboxes_wide_image_in_tall_container() {
        val f = fitRect(containerW = 400f, containerH = 800f, imageW = 200, imageH = 100)
        assertEquals(0f, f.left, eps)
        assertEquals(400f, f.width, eps)
        assertEquals(200f, f.height, eps)
        assertEquals(300f, f.top, eps)
    }

    @Test
    fun fit_rect_pillarboxes_tall_image_in_wide_container() {
        val f = fitRect(containerW = 800f, containerH = 400f, imageW = 100, imageH = 200)
        assertEquals(0f, f.top, eps)
        assertEquals(400f, f.height, eps)
        assertEquals(200f, f.width, eps)
        assertEquals(300f, f.left, eps)
    }
}
