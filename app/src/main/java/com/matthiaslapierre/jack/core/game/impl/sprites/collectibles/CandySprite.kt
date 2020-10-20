package com.matthiaslapierre.jack.core.game.impl.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.CANDY_WIDTH
import com.matthiaslapierre.jack.Constants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.impl.sprites.player.PlayerSprite

/**
 * Candy to collect.
 */
class CandySprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float
): Sprite {

    var isCollected: Boolean = false

    private val candyImage: Image = resourceManager.getRandomCandy()
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var isAlive: Boolean = true
    private var explosionFrame: Int = 0
    private var animateExplosionEnded: Boolean = false

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        val explosionImages = resourceManager.collectibleExplosion!!

        if (width == UNDEFINED) {
            width = screenWidth * CANDY_WIDTH
            height = width * candyImage.height / candyImage.width
        }

        isAlive = (y <= (screenHeight * SPRITE_LIFE_LOWEST_Y) && (!isCollected
                || !animateExplosionEnded))

        if (status == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        // Draw the candy if it is not collected or the explosion animation is not ended.
        if (!isCollected || explosionFrame < explosionImages.size / 2) {
            canvas.drawBitmap(
                candyImage.bitmap,
                candyImage.rect,
                getRectF(),
                globalPaint
            )
        }

        if (isCollected) {
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
            && !isCollected

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