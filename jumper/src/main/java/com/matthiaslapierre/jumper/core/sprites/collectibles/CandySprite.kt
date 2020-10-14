package com.matthiaslapierre.jumper.core.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.CANDY_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite

internal class CandySprite(
    resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float
): Sprite {

    var isCollected: Boolean = false

    private val candyImage: Image = resourceManager.getRandomCandy()
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()

        if (width == UNDEFINED) {
            width = screenWidth * CANDY_WIDTH
            height = width * candyImage.height / candyImage.width
        }

        isAlive = (y <= (screenWidth * SPRITE_LIFE_LOWEST_Y) && !isCollected)

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        val srcRect = Rect(
            0,
            0,
            candyImage.width,
            candyImage.height
        )
        val dstRect = getRectF()
        canvas.drawBitmap(
            candyImage.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )
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