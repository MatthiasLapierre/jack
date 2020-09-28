package com.matthiaslapierre.framework.ui

import android.graphics.Canvas
import android.graphics.Paint
import com.matthiaslapierre.framework.resources.Image

interface Graphics {
    enum class ImageFormat {
        ARGB8888, ARGB4444, RGB565
    }

    fun newImage(fileName: String, format: ImageFormat): Image

    fun clearScreen(color: Int)

    fun drawLine(x: Int, y: Int, x2: Int, y2: Int, color: Int)

    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int)

    fun drawImage(
        image: Image,
        x: Int,
        y: Int,
        srcX: Int,
        srcY: Int,
        srcWidth: Int,
        srcHeight: Int
    )

    fun drawImage(Image: Image, x: Int, y: Int)

    fun drawString(
        text: String,
        x: Int,
        y: Int,
        paint: Paint
    )

    fun drawARGB(i: Int, j: Int, k: Int, l: Int)

    fun getWidth(): Int

    fun getHeight(): Int

    fun getCanvas(): Canvas
}