package com.matthiaslapierre.jumper.core.sprites.obstacles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.BAT_FRAME_RATE
import com.matthiaslapierre.jumper.JumperConstants.BAT_SPEED
import com.matthiaslapierre.jumper.JumperConstants.BAT_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite

internal class BatSprite(
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

        if(maxX - minX > width) {
            x += speed
            if (x < minX || x > maxX) {
                speed = -speed
            }
        }

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        if (!isDestroyed || explosionFrame < explosionImages.size / 2) {
            canvas.drawBitmap(
                batImage.bitmap,
                Rect(
                    0,
                    0,
                    batImage.width,
                    batImage.height
                ),
                getRectF(),
                globalPaint
            )

            if(System.currentTimeMillis() - lastFrameTimestamp > BAT_FRAME_RATE) {
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
                Rect(
                    0,
                    0,
                    explosionImage.width,
                    explosionImage.height
                ),
                RectF(
                    x - (width / 2f),
                    y - (width / 2f),
                    x + (width / 2f),
                    y + (width / 2f)
                ),
                globalPaint
            )
            if(explosionFrame == explosionImages.size - 1) {
                animateExplosionEnded = true
            } else {
                explosionFrame++
            }
        }
    }

    override fun isAlive(): Boolean = isAlive

    override fun isHit(sprite: Sprite): Boolean = sprite is PlayerSprite
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

}