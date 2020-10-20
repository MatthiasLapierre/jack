package com.matthiaslapierre.jack.core.game.impl.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.utils.getRect

/**
 * Background.
 */
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
        get() = gameStates.backgroundSpeedY

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

        canvas.drawBitmap(
            bitmap,
            bitmap.getRect(),
            getRectF(),
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

}