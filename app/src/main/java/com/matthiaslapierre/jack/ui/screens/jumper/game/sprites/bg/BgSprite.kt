package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants

abstract class BgSprite : Sprite {

    private var x: Int = Constants.UNDEFINED
    private var y: Int = Constants.UNDEFINED
    private var width: Int = Constants.UNDEFINED
    private var height: Int = Constants.UNDEFINED
    private var speed: Float = 0f

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val bitmap = getBackgroundBitmap()

        if(y == Constants.UNDEFINED) {
            val screenWidth = canvas.width
            val screenHeight = canvas.height

            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            width = screenWidth
            height = (width * originalHeight / originalWidth.toFloat()).toInt()
            x = ((screenWidth - width) / 2f).toInt()
            y = screenHeight - height
        }

        val srcRect = Rect(
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        val dstRect = getRect()

        canvas.drawBitmap(
            bitmap,
            srcRect,
            dstRect,
            globalPaint
        )
    }

    override fun isAlive(): Boolean = true

    override fun isHit(sprite: Sprite): Boolean = false

    override fun getScore(): Int = 0

    override fun getRect(): Rect = Rect(
        x,
        y,
        x + width,
        y + height
    )

    override fun onDispose() {

    }

    protected abstract fun getBackgroundBitmap(): Bitmap

}