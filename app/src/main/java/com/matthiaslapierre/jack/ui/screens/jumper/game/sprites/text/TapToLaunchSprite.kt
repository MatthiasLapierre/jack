package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.jack.core.ResourceManager

class TapToLaunchSprite(
    private val resourceManager: ResourceManager
) : Sprite {

    companion object {
        private const val Y_RATIO = .25f
        private const val WIDTH_RATIO = .9f
    }

    private var x: Int = Constants.UNDEFINED
    private var y: Int = Constants.UNDEFINED
    private var width: Int = Constants.UNDEFINED
    private var height: Int = Constants.UNDEFINED
    private var isAlive = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        isAlive = status == Sprite.Status.STATUS_NOT_STARTED

        val image = resourceManager.textTapToLaunch!!

        val screenWidth = canvas.width
        val screenHeight = canvas.height
        if (x == Constants.UNDEFINED) {
            val originalWidth = image.width
            val originalHeight = image.height
            width = (screenWidth * WIDTH_RATIO).toInt()
            height = (width * originalHeight / originalWidth.toFloat()).toInt()
            x = ((screenWidth - width) / 2f).toInt()
            y = (screenHeight * Y_RATIO).toInt()
        }

        val srcRect = Rect(
            0,
            0,
            image.bitmap.width,
            image.bitmap.height
        )
        val dstRect = getRect()

        canvas.drawBitmap(
            image.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )
    }

    override fun isAlive(): Boolean = isAlive

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

}