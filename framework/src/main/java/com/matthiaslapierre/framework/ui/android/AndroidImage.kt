package com.matthiaslapierre.framework.ui.android

import android.graphics.Bitmap
import com.matthiaslapierre.framework.ui.Graphics
import com.matthiaslapierre.framework.resources.Image

class AndroidImage(
    private val mBitmap: Bitmap,
    private val mFormat: Graphics.ImageFormat
) : Image {

    override val bitmap: Bitmap
        get() = mBitmap

    override val format: Graphics.ImageFormat
        get() = mFormat

    override val width: Int
        get() = bitmap.width

    override val height: Int
        get() = bitmap.height

    override fun dispose() {
        bitmap.recycle()
    }

}
