package com.matthiaslapierre.jumper.core.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PowerUp
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.POWER_UP_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite

internal class PowerUpSprite(
    resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float,
    val powerUp: Int
): Sprite {

    companion object {
        private fun getPowerUpImage(resourceManager: ResourceManager, powerUp: Int): Image {
            val resId = when (powerUp) {
                GameStates.POWER_UP_ARMORED -> PowerUp.ARMORED
                GameStates.POWER_UP_COPTER -> PowerUp.COPTER
                GameStates.POWER_UP_MAGNET -> PowerUp.MAGNET
                GameStates.POWER_UP_ROCKET -> PowerUp.ROCKET
                else -> PowerUp.ARMORED
            }
            return resourceManager.powerUps!![resId]!!
        }
    }

    var isConsumed: Boolean = false

    private val powerUpImage: Image = getPowerUpImage(resourceManager, powerUp)
    private var width: Float = Constants.UNDEFINED
    private var height: Float = Constants.UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()

        if (width == Constants.UNDEFINED) {
            width = screenWidth * POWER_UP_WIDTH
            height = width * powerUpImage.height / powerUpImage.width
        }

        isAlive = y <= (screenWidth * SPRITE_LIFE_LOWEST_Y) && !isConsumed

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        val srcRect = Rect(
            0,
            0,
            powerUpImage.width,
            powerUpImage.height
        )
        val dstRect = getRectF()
        canvas.drawBitmap(
            powerUpImage.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )
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