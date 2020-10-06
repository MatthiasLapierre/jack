package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.collectibles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.game.GameStates

class CandySprite(
    resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .15f
    }

    private val candyImage: Image = resourceManager.getRandomCandy()
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        if (width == UNDEFINED) {
            width = screenWidth * WIDTH_RATIO
            height = width * candyImage.height / candyImage.width
        }

        isAlive = y <= (screenHeight * 2f)

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speed
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

    override fun isHit(sprite: Sprite): Boolean {
        return false
    }

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