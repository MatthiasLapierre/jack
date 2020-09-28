package com.matthiaslapierre.framework.ui.android

import android.content.res.AssetManager
import android.graphics.*
import com.matthiaslapierre.framework.ui.Graphics
import com.matthiaslapierre.framework.resources.Image

class AndroidGraphics(
    var assets: AssetManager,
    var frameBuffer: Bitmap
) : Graphics {

    private val mCanvas: Canvas = Canvas(frameBuffer)
    private val mPaint: Paint = Paint()
    private val mSrcRect = Rect()
    private val mDstRect = Rect()

    override fun newImage(fileName: String, format: Graphics.ImageFormat): Image {
        val config: Bitmap.Config = when {
            format == Graphics.ImageFormat.RGB565 -> Bitmap.Config.RGB_565
            format === Graphics.ImageFormat.ARGB4444 -> Bitmap.Config.ARGB_4444
            else -> Bitmap.Config.ARGB_8888
        }

        val options = BitmapFactory.Options()
        options.inPreferredConfig = config

        val bitmap: Bitmap = assets.open(fileName).use { inputStream ->
            return@use BitmapFactory.decodeStream(inputStream, null, options)
                ?: throw RuntimeException("Couldn't load bitmap from asset '$fileName'")
        }

        return AndroidImage(bitmap, format)
    }

    override fun clearScreen(color: Int) {
        mCanvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR)
    }

    override fun drawLine(x: Int, y: Int, x2: Int, y2: Int, color: Int) {
        mPaint.color = color
        mCanvas.drawLine(x.toFloat(), y.toFloat(), x2.toFloat(), y2.toFloat(), mPaint)
    }

    override fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
        mCanvas.drawRect(
            x.toFloat(),
            y.toFloat(),
            x + width - 1.toFloat(),
            y + height - 1.toFloat(),
            mPaint
        )
    }

    override fun drawARGB(a: Int, r: Int, g: Int, b: Int) {
        mPaint.style = Paint.Style.FILL
        mCanvas.drawARGB(a, r, g, b)
    }

    override fun drawString(
        text: String,
        x: Int,
        y: Int,
        paint: Paint
    ) {
        mCanvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    }

    override fun drawImage(
        Image: Image, x: Int, y: Int, srcX: Int, srcY: Int,
        srcWidth: Int, srcHeight: Int
    ) {
        mSrcRect.left = srcX
        mSrcRect.top = srcY
        mSrcRect.right = srcX + srcWidth
        mSrcRect.bottom = srcY + srcHeight
        mDstRect.left = x
        mDstRect.top = y
        mDstRect.right = x + srcWidth
        mDstRect.bottom = y + srcHeight
        mCanvas.drawBitmap(
            (Image as AndroidImage).bitmap, mSrcRect, mDstRect,
            null
        )
    }

    override fun drawImage(image: Image, x: Int, y: Int) {
        mCanvas.drawBitmap((image as AndroidImage).bitmap, x.toFloat(), y.toFloat(), null)
    }

    override fun getWidth(): Int {
        return frameBuffer.width
    }

    override fun getHeight(): Int {
        return frameBuffer.height
    }

    override fun getCanvas(): Canvas {
        return mCanvas
    }

}
