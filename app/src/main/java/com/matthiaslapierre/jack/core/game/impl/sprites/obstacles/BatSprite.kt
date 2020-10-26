package com.matthiaslapierre.jack.core.game.impl.sprites.obstacles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.BAT_BODY_INSET_X
import com.matthiaslapierre.jack.Constants.BAT_BODY_INSET_Y
import com.matthiaslapierre.jack.Constants.BAT_FRAME_RATE
import com.matthiaslapierre.jack.Constants.BAT_WIDTH
import com.matthiaslapierre.jack.Constants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.impl.sprites.player.PlayerSprite
import com.matthiaslapierre.jack.core.resources.ResourceManager

/**
 * Bat obstacle.
 */
class BatSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float,
    private var minX: Float,
    private var maxX: Float
) : Sprite {

    private var frame: Int = 0
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private val speedX: Float
        get() = gameStates.batSpeedX * speedXMultiplier
    private var speedXMultiplier: Int = 1
    private var lastFrameTimestamp: Long = 0L
    private var isAlive: Boolean = true
    private var isDestroyed: Boolean = false
    private var explosionFrame: Int = 0
    private var animateExplosionEnded: Boolean = false

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val batImages = resourceManager.bat!!
        val batImage = batImages[frame]
        val explosionImages = resourceManager.collectibleExplosion!!

        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (width == UNDEFINED) {
            width = screenWidth * BAT_WIDTH
            height = width * batImage.height / batImage.width
        }

        isAlive = (y <= (screenHeight * SPRITE_LIFE_LOWEST_Y) && (!isDestroyed
                || !animateExplosionEnded))

        // Update the sprite position.
        if (status == Sprite.Status.STATUS_PLAY) {
            if(maxX - minX > width) {
                x += speedX
                if (x < minX || x > maxX) {
                    speedXMultiplier = -speedXMultiplier
                }
            }
            y += gameStates.speedY
        }

        // Draw the bat if it is not destroyed or the explosion animation is not ended.
        if (!isDestroyed || explosionFrame < explosionImages.size / 2) {
            canvas.drawBitmap(
                batImage.bitmap,
                batImage.rect,
                getRectF(),
                globalPaint
            )

            if(status != Sprite.Status.STATUS_PAUSE
                && System.currentTimeMillis() - lastFrameTimestamp > BAT_FRAME_RATE) {
                frame++
                if (frame >= batImages.size) {
                    frame = 0
                }
                lastFrameTimestamp = System.currentTimeMillis()
            }
        }

        if (isDestroyed) {
            // Play the explosion animation.
            val explosionImage = explosionImages[explosionFrame]
            canvas.drawBitmap(
                explosionImage.bitmap,
                explosionImage.rect,
                RectF(
                    x - (width / 2f),
                    y - (width / 2f),
                    x + (width / 2f),
                    y + (width / 2f)
                ),
                globalPaint
            )
            if (status != Sprite.Status.STATUS_PAUSE) {
                if (explosionFrame == explosionImages.size - 1) {
                    animateExplosionEnded = true
                } else {
                    explosionFrame++
                }
            }
        }
    }

    override fun isAlive(): Boolean = isAlive

    override fun isHit(sprite: Sprite): Boolean = sprite is PlayerSprite
            && sprite.getBodyRectF().intersect(getBodyRectF())
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

    fun blowOnTheHead(sprite: Sprite): Boolean = sprite is PlayerSprite
            && gameStates.direction == GameStates.Direction.DOWN
            && sprite.getFeetRectF().intersect(getBodyRectF())

    private fun getBodyRectF(): RectF = getRectF().run {
        return RectF(
            left + (width * BAT_BODY_INSET_X),
            top + (height * BAT_BODY_INSET_Y),
            right - (width * BAT_BODY_INSET_X),
            bottom - (height * BAT_BODY_INSET_Y)
        )
    }

    fun destroy() {
        isDestroyed = true
    }

}