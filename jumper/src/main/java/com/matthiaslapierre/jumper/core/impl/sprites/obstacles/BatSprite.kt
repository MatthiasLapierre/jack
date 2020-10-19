package com.matthiaslapierre.jumper.core.impl.sprites.obstacles

import android.graphics.*
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.BAT_BODY_INSET_X
import com.matthiaslapierre.jumper.JumperConstants.BAT_BODY_INSET_Y
import com.matthiaslapierre.jumper.JumperConstants.BAT_FRAME_RATE
import com.matthiaslapierre.jumper.JumperConstants.BAT_SPEED
import com.matthiaslapierre.jumper.JumperConstants.BAT_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.JumperGameStates
import com.matthiaslapierre.jumper.core.impl.sprites.player.PlayerSprite

internal class BatSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: JumperGameStates,
    override var x: Float,
    override var y: Float,
    private var minX: Float,
    private var maxX: Float
) : Sprite {

    private var frame: Int = 0
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var speed: Float = UNDEFINED
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
            speed = width * BAT_SPEED
        }

        isAlive = (y <= (screenHeight * SPRITE_LIFE_LOWEST_Y) && (!isDestroyed
                || !animateExplosionEnded))

        if (status == Sprite.Status.STATUS_PLAY) {
            if(maxX - minX > width) {
                x += speed
                if (x < minX || x > maxX) {
                    speed = -speed
                }
            }
            y += gameStates.speedY
        }

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
            && gameStates.direction == JumperGameStates.Direction.DOWN
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