package com.matthiaslapierre.jack.core.game.impl.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.CLOUD_MAX_WIDTH
import com.matthiaslapierre.jack.Constants.CLOUD_MIN_WIDTH
import com.matthiaslapierre.jack.Constants.CLOUD_OUTSET
import com.matthiaslapierre.jack.Constants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.utils.Utils

/**
 * Cloud.
 */
class CloudSprite(
    resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var y: Float
): Sprite {

    override var x: Float = UNDEFINED

    private val cloudImage: Image = resourceManager.getRandomCloud()
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private val speed: Float
        get() = gameStates.cloudSpeedY
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (width == UNDEFINED) {
            width = Utils.getRandomFloat(screenWidth * CLOUD_MIN_WIDTH, screenWidth * CLOUD_MAX_WIDTH)
            height = width *  cloudImage.height / cloudImage.width
            val outset = (screenWidth * CLOUD_OUTSET)
            x = Utils.getRandomFloat(outset + (width / 2f), screenWidth - outset - (width / 2f))
        }

        isAlive = y <= (screenHeight * SPRITE_LIFE_LOWEST_Y)

        if (status == Sprite.Status.STATUS_PLAY) {
            y += speed
        }

        canvas.drawBitmap(
            cloudImage.bitmap,
            cloudImage.rect,
            getRectF(),
            globalPaint
        )
    }

    override fun isAlive(): Boolean  = isAlive

    override fun isHit(sprite: Sprite): Boolean = false

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = RectF(
        x - (width / 2f),
        y - (height / 2f),
        x + (width / 2f),
        y + (height / 2f)
    )

    override fun onDispose() {

    }

}