package com.matthiaslapierre.jumper.core.sprites.player

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.GameStates

class PlayerSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .27f
        private const val BOTTOM_RATIO = .35f
        private const val HIGHEST_Y_RATIO = 0.6f
        private const val FRAME_PER_MS = 120
    }

    override var x: Float = UNDEFINED
    override var y: Float = UNDEFINED

    private var state: ResourceManager.PlayerState = ResourceManager.PlayerState.JUMP
    private var frame: Int = 0
    private var highestY: Float = UNDEFINED
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED
    private var lastFrameTimestamp: Long = 0L

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val images = resourceManager.player!![state]!!
        val image = images[frame]!!

        val screenWidth = canvas.width.toFloat()
        screenHeight = canvas.height.toFloat()
        if (x == UNDEFINED) {
            width = screenWidth * WIDTH_RATIO
            height = width * image.height / image.width.toFloat()
            x = (screenWidth - width) / 2f
            y = screenHeight - (screenHeight * BOTTOM_RATIO) - (height / 2f)
            highestY = (screenHeight - height) * HIGHEST_Y_RATIO
        }

        if ((status == Sprite.Status.STATUS_PLAY
                    && gameStates.state == GameStates.State.LAUNCHED)
            || status == Sprite.Status.STATUS_GAME_OVER && y > 0) {
            x -= gameStates.speedX
            y -= gameStates.speedY
            if (x > screenWidth) {
                x = -width
            } else if(x < -width) {
                x = screenWidth
            }
            if (y < highestY) {
                y = highestY
            }
        }

        val srcRect = Rect(
            0,
            0,
            image.bitmap.width,
            image.bitmap.height
        )
        val dstRect = getRectF()

        canvas.drawBitmap(
            image.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )

        if(System.currentTimeMillis() - lastFrameTimestamp > FRAME_PER_MS) {
            frame++
            if (frame >= images.size) {
                frame = 0
            }
            lastFrameTimestamp = System.currentTimeMillis()
        }
    }

    override fun isAlive(): Boolean = true

    override fun isHit(sprite: Sprite): Boolean = false

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = if (gameStates.state == GameStates.State.READY_TO_LAUNCH) {
        RectF(0f,0f,0f,0f)
    } else {
        RectF(
            x,
            y,
            x + width,
            y + height
        )
    }

    override fun onDispose() {

    }

    fun isDead() = y > screenHeight

}