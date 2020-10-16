package com.matthiaslapierre.framework.ui.android

import android.graphics.Bitmap
import android.graphics.Rect
import com.matthiaslapierre.framework.resources.Image

class AndroidImage(
    private val mBitmap: Bitmap
) : Image {

    override val bitmap: Bitmap
        get() = mBitmap

    override val width: Int
        get() = bitmap.width

    override val height: Int
        get() = bitmap.height

    override val rect: Rect
        get() = Rect(0,0,width,height)

    override fun dispose() {
        bitmap.recycle()
    }

}
