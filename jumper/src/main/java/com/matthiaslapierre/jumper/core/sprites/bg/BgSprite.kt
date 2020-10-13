package com.matthiaslapierre.jumper.core.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.GameStates

class BgSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
) : Sprite {

    override var x: Float = UNDEFINED
    override  var y: Float = UNDEFINED

    private var maxY: Float = 0f
    private var minY: Float = UNDEFINED
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED
    private val speed: Float
        get() = gameStates.speedY * getAcceleration()

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val bitmap = resourceManager.bgJump!!.bitmap

        if(y == UNDEFINED) {
            val screenWidth = canvas.width.toFloat()
            screenHeight = canvas.height.toFloat()

            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            width = screenWidth
            height = width * originalHeight / originalWidth
            x = (screenWidth - width) / 2f
            y = screenHeight - height
            minY = y
        }

        if (status == Sprite.Status.STATUS_PLAY) {
            y += speed
            if (y < minY) {
                y = minY
            } else if (y > maxY) {
                y = maxY
            }
        }

        val srcRect = Rect(
            0,
            0,
            bitmap.width,
            bitmap.height
        )
        val dstRect = getRectF()

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

    override fun getRectF(): RectF = RectF(
        x,
        y,
        x + width,
        y + height
    )

    override fun onDispose() {

    }

    private fun getAcceleration(): Float  = 1f
        /*val multiplier = 1f - ((1f / (height + screenHeight)) * (height - y))
        return when {
            multiplier > 1f -> {
                1f
            }
            multiplier < 0f -> {
                0f
            }
            else -> {
                multiplier
            }
        }
    }*/

}