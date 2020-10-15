package com.matthiaslapierre.jumper.core.sprites.obstacles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.JumperConstants.SPIKE_HIGHEST_Y
import com.matthiaslapierre.jumper.JumperConstants.SPIKE_WRAITH_DURATION
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite
import com.matthiaslapierre.utils.Utils

internal class SpikeSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var y: Float
): Sprite {

    enum class State {
        SHOW,
        HIDE,
        IDLE
    }

    override var x: Float = UNDEFINED

    private var minX: Float = UNDEFINED
    private var maxX: Float = UNDEFINED
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var smokeFrame: Int = 0
    private var isAlive: Boolean = true
    private var isDestroyed: Boolean = false
    private var wraithDuration: Long = 0L
    private var lastShowingTimestamp: Long = 0L
    private var state: State = State.IDLE

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val spikeImage = resourceManager.spike!!
        val smokeImages = resourceManager.smoke!!

        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (x == UNDEFINED) {
            width = screenWidth * JumperConstants.SPIKE_WIDTH
            height = width * spikeImage.height / spikeImage.width
            minX = width / 2f
            maxX = screenWidth - (width / 2f)
            x = Utils.getRandomFloat(minX, maxX)
        }

        isAlive = y <= (screenHeight * JumperConstants.SPRITE_LIFE_LOWEST_Y)
                && (!isDestroyed || smokeFrame < smokeImages.size - 1)

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        when(state) {
            State.SHOW -> if (smokeFrame > smokeImages.size / 2) {
                drawSpike(canvas, globalPaint, spikeImage)
            }
            State.HIDE -> if (smokeFrame < smokeImages.size / 2) {
                drawSpike(canvas, globalPaint, spikeImage)
            }
            else -> if(y > screenHeight * SPIKE_HIGHEST_Y) {
                state = State.SHOW
            }
        }

        if (state == State.IDLE) {
            return
        }

        if (smokeFrame < smokeImages.size - 1) {
            val smokeImage = smokeImages[smokeFrame]
            canvas.drawBitmap(
                smokeImage.bitmap,
                Rect(
                    0,
                    0,
                    smokeImage.width,
                    smokeImage.height
                ),
                RectF(
                    x - (width / 2f),
                    y - (width / 2f),
                    x + (width / 2f),
                    y + (width / 2f)
                ),
                globalPaint
            )
            smokeFrame++
        }

        if (state == State.SHOW && !isDestroyed) {
            val now = System.currentTimeMillis()
            if (lastShowingTimestamp != 0L) {
                val interval = now - lastShowingTimestamp
                if (interval < 1000) {
                    wraithDuration += interval
                }
            }
            lastShowingTimestamp = now
        }
        val newState = if (wraithDuration > SPIKE_WRAITH_DURATION * 1000L || isDestroyed) {
            State.HIDE
        } else {
            State.SHOW
        }
        if (state != newState && smokeFrame == smokeImages.size - 1) {
            wraithDuration = 0L
            lastShowingTimestamp = 0L
            smokeFrame = 0
            state = newState
            if (state == State.SHOW) {
                x = Utils.getRandomFloat(minX, maxX)
            }
        }
    }

    override fun isAlive(): Boolean = isAlive

    override fun isHit(sprite: Sprite): Boolean = sprite is PlayerSprite
            && state == State.SHOW
            && sprite.getBodyRectF().intersect(getRectF())
            && !isDestroyed

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = RectF(
        x - (width / 2f),
        y - (height / 2f),
        x + (width / 2f),
        y + (height / 2f)
    )

    override fun onDispose() {

    }

    fun destroy() {
        isDestroyed = true
    }

    private fun drawSpike(canvas: Canvas, globalPaint: Paint, spikeImage: Image) {
        canvas.drawBitmap(
            spikeImage.bitmap,
            Rect(
                0,
                0,
                spikeImage.width,
                spikeImage.height
            ),
            getRectF(),
            globalPaint
        )
    }

}