package com.matthiaslapierre.jumper.core.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PlayerPowerUp
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite

class PowerUpSprite(
    resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float,
    powerUp: PlayerPowerUp
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .15f
    }

    private val powerUpImage: Image = resourceManager.powerUps!![powerUp]!!
    private var width: Float = Constants.UNDEFINED
    private var height: Float = Constants.UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        if (width == Constants.UNDEFINED) {
            width = screenWidth * WIDTH_RATIO
            height = width * powerUpImage.height / powerUpImage.width
        }

        isAlive = y <= (screenHeight * SPRITE_LIFE_LOWEST_Y)

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
            && sprite.getRectF().intersect(getRectF())

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