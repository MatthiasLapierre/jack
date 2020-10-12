package com.matthiaslapierre.jumper.core.sprites.player

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.core.ResourceManager.PlayerState.IDLE
import com.matthiaslapierre.core.ResourceManager.PlayerState.LAUNCH
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.GameStates

class CannonSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    private var cannonInterface: CannonInterface?
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .5f
        private const val BOTTOM_RATIO = .30f
        private const val IDLE_FRAME_PER_MS = 120
        private const val LAUNCH_FRAME_PER_MS = 20
    }

    override var x: Float = UNDEFINED
    override var y: Float = UNDEFINED

    private var state: PlayerState = IDLE
    private var frame: Int = 0
    private var highestY: Float = UNDEFINED
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var lastFrameTimestamp: Long = 0L
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val newState = if(status == Sprite.Status.STATUS_NOT_STARTED) {
            IDLE
        } else {
            LAUNCH
        }
        if(state != newState) {
            state = newState
            frame = 0
        }
        val images = resourceManager.player!![state]!!
        val image = images[frame]!!

        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (x == UNDEFINED) {
            width = screenWidth * WIDTH_RATIO
            height = width * image.height / image.width
            x = (screenWidth - width) / 2f
            y = screenHeight - (screenHeight * BOTTOM_RATIO) - (height / 2f)
            highestY = y
        }

        isAlive = y < screenHeight

        if (status == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
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

        if (state == LAUNCH && frame == 4) {
            cannonInterface?.onFire()
        }

        val frameDuration = if (state == IDLE) {
            IDLE_FRAME_PER_MS
        } else {
            LAUNCH_FRAME_PER_MS
        }

        if(System.currentTimeMillis() - lastFrameTimestamp > frameDuration) {
            frame++
            if (frame >= images.size) {
                frame = if(state == LAUNCH) {
                    images.size - 1
                } else {
                    0
                }
            }
            lastFrameTimestamp = System.currentTimeMillis()
        }
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
        cannonInterface = null
    }

    interface CannonInterface {
        fun onFire()
    }

}