package com.matthiaslapierre.jumper.core.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.POWER_UP_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite
import com.matthiaslapierre.jumper.utils.JumperUtils

internal class PowerUpSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float,
    val powerUp: Int
): Sprite {

    companion object {
        private fun getPowerUpImage(resourceManager: ResourceManager, flag: Int): Image {
            val resId = JumperUtils.getFlagToPowerUp(flag)
            return resourceManager.powerUps!![resId]!!
        }
    }

    var isConsumed: Boolean = false

    private val powerUpImage: Image = getPowerUpImage(resourceManager, powerUp)
    private var width: Float = Constants.UNDEFINED
    private var height: Float = Constants.UNDEFINED
    private var isAlive: Boolean = true
    private var explosionFrame: Int = 0
    private var animateExplosionEnded: Boolean = false

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        val explosionImages = resourceManager.collectibleExplosion!!

        if (width == Constants.UNDEFINED) {
            width = screenWidth * POWER_UP_WIDTH
            height = width * powerUpImage.height / powerUpImage.width
        }

        isAlive = (y <= (screenHeight * SPRITE_LIFE_LOWEST_Y) && (!isConsumed
                || !animateExplosionEnded))

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        if (!isConsumed || explosionFrame < explosionImages.size / 2) {
            canvas.drawBitmap(
                powerUpImage.bitmap,
                Rect(
                    0,
                    0,
                    powerUpImage.width,
                    powerUpImage.height
                ),
                getRectF(),
                globalPaint
            )
        }

        if (isConsumed) {
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
            && !isConsumed

    override fun getScore(): Int = 1

    override fun getRectF(): RectF = RectF(
        x - (width / 2f),
        y - (height / 2f),
        x + (width / 2f),
        y + (height / 2f)
    )

    override fun onDispose() {

    }

}