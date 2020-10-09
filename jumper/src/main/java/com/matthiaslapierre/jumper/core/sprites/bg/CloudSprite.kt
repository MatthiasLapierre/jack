package com.matthiaslapierre.jumper.core.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.utils.JumperUtils
import com.matthiaslapierre.utils.Utils

class CloudSprite(
    resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var y: Float
): Sprite {

    companion object {
        private const val OUTSET: Float = 0.05f
        private const val ACCELERATION: Float = 0.5f
        const val ORIGINAL_RESOLUTION_WIDTH = 960f
    }

    override var x: Float = UNDEFINED

    private val cloudImage: Image = resourceManager.getRandomCloud()
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (width == UNDEFINED) {
            val size = JumperUtils.getScaledSize(
                cloudImage,
                screenWidth.toInt(),
                ORIGINAL_RESOLUTION_WIDTH.toInt()
            )
            width = size.first.toFloat()
            height = size.second.toFloat()
            val outset = (screenWidth * OUTSET)
            x = Utils.getRandomFloat(outset + (width / 2f), screenWidth - outset - (width / 2f))
        }

        isAlive = y <= (screenHeight * 2f)

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speed * ACCELERATION
        }

        val srcRect = Rect(
            0,
            0,
            cloudImage!!.width,
            cloudImage!!.height
        )
        val dstRect = getRectF()

        canvas.drawBitmap(
            cloudImage!!.bitmap,
            srcRect,
            dstRect,
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