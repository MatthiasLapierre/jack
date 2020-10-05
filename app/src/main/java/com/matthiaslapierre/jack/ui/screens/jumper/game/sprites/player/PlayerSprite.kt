package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.player

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.core.ResourceManager.PlayerState
import com.matthiaslapierre.jack.ui.screens.jumper.game.GameStates

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

    private var state: PlayerState = PlayerState.JUMP
    private var frame: Int = 0
    private var x: Float = UNDEFINED
    private var y: Float = UNDEFINED
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
                    && gameStates.playerState == GameStates.PlayerState.LAUNCHED)
            || status == Sprite.Status.STATUS_GAME_OVER && y > 0) {
            y -= gameStates.speed
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

    override fun getRectF(): RectF = if (gameStates.playerState == GameStates.PlayerState.READY_TO_LAUNCH) {
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