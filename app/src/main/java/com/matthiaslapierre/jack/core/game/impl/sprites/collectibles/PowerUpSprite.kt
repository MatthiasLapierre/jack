package com.matthiaslapierre.jack.core.game.impl.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.matthiaslapierre.jack.Constants
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.POWER_UP_WIDTH
import com.matthiaslapierre.jack.Constants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.utils.Utils
import com.matthiaslapierre.jack.core.game.impl.sprites.player.PlayerSprite

/**
 * Bonus candy.
 */
class PowerUpSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float,
    val powerUp: Int
): Sprite {

    companion object {
        private fun getPowerUpImage(resourceManager: ResourceManager, flag: Int): Image {
            val resId = Utils.getFlagToPowerUpResId(flag)
            return resourceManager.powerUpsResId!![resId]!!
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

        if (status == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        // Draw the candy if it is not collected or the explosion animation is not ended.
        if (!isConsumed || explosionFrame < explosionImages.size / 2) {
            canvas.drawBitmap(
                powerUpImage.bitmap,
                powerUpImage.rect,
                getRectF(),
                globalPaint
            )
        }

        if (isConsumed) {
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