package com.matthiaslapierre.framework.resources

import android.graphics.Bitmap

interface Image {
    val bitmap: Bitmap
    val width: Int
    val height: Int
    fun dispose()
}