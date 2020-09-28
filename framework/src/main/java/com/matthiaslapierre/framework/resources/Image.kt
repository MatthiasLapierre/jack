package com.matthiaslapierre.framework.resources

import android.graphics.Bitmap
import com.matthiaslapierre.framework.ui.Graphics

interface Image {
    val bitmap: Bitmap
    val format: Graphics.ImageFormat
    val width: Int
    val height: Int
    fun dispose()
}