package com.sunny.gallery.crop.callback

import android.graphics.RectF

interface OverlayViewChangeListener {
    fun onCropRectUpdated(cropRect: RectF)
}