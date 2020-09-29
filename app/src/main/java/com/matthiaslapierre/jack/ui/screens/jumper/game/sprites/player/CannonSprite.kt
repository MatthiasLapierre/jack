package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.player

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState.IDLE
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState.LAUNCH

class CannonSprite(
    private val resourceManager: ResourceManager
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .5f
        private const val BOTTOM_RATIO = .30f
    }

    private var state: PlayerState = IDLE
    private var frame: Int = 0
    private var x: Int = UNDEFINED
    private var y: Int = UNDEFINED
    private var width: Int = UNDEFINED
    private var height: Int = UNDEFINED
    private var isAlive = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val image = resourceManager.player!![state]!![frame]!!

        val screenWidth = canvas.width
        val screenHeight = canvas.height
        if (x == UNDEFINED) {
            width = (screenWidth * WIDTH_RATIO).toInt()
            height = (width * image.height / image.width.toFloat()).toInt()
            x = ((screenWidth - width) / 2f).toInt()
            y = (screenHeight - (screenHeight * BOTTOM_RATIO) - (height / 2f)).toInt()
        }

        isAlive = y < screenHeight

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

        frame++
        if (state == IDLE && frame > 3) {
            frame = 0
        } else if (state == LAUNCH && frame > 8) {
            frame = 9
        }
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