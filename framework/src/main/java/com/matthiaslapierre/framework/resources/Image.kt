package com.matthiaslapierre.framework.resources

import android.graphics.Bitmap
import android.graphics.Rect

/**
 * Handles an image resource.
 */
interface Image {
    val bitmap: Bitmap
    val width: Int
    val height: Int
    val rect: Rect
    fun dispose()
}