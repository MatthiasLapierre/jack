package com.matthiaslapierre.framework.resources

import android.graphics.Bitmap
import android.graphics.Rect

interface Image {
    val bitmap: Bitmap
    val width: Int
    val height: Int
    val rect: Rect
    fun dispose()
}