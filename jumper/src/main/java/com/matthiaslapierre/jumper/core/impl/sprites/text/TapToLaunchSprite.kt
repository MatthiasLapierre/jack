package com.matthiaslapierre.jumper.core.impl.sprites.text

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite

internal class TapToLaunchSprite(
    private val resourceManager: ResourceManager
) : Sprite {

    companion object {
        private const val Y_RATIO = .25f
        private const val WIDTH_RATIO = .9f
    }

    override var x: Float = Constants.UNDEFINED
    override var y: Float = Constants.UNDEFINED

    private var width: Float = Constants.UNDEFINED
    private var height: Float = Constants.UNDEFINED
    private var isAlive = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        isAlive = status == Sprite.Status.STATUS_NOT_STARTED

        val image = resourceManager.textTapToLaunch!!

        val screenWidth = canvas.width
        val screenHeight = canvas.height
        if (x == Constants.UNDEFINED) {
            val originalWidth = image.width
            val originalHeight = image.height
            width = screenWidth * WIDTH_RATIO
            height = width * originalHeight / originalWidth
            x = (screenWidth - width) / 2f
            y = screenHeight * Y_RATIO
        }

        canvas.drawBitmap(
            image.bitmap,
            image.rect,
            getRectF(),
            globalPaint
        )
    }

    override fun isAlive(): Boolean = isAlive

    override fun isHit(sprite: Sprite): Boolean = false

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = RectF(
        x,
        y,
        x + width,
        y + height
    )

    override fun onDispose() {

    }

}