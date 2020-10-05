package com.matthiaslapierre.jack.ui.screens.jumper.game.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.ResourceManager
import com.matthiaslapierre.jack.ui.screens.jumper.game.GameStates
import com.matthiaslapierre.jack.utils.Utils

class CloudSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    var y: Float
): Sprite {

    companion object {
        private const val OUTSET: Float = 0.05f
        private const val ACCELERATION: Float = 0.5f
    }

    private var cloudImage: Image? = null
    private var x: Float = UNDEFINED
    private var width = UNDEFINED
    private var height = UNDEFINED
    private var isAlive = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (cloudImage == null) {
            cloudImage = getRandomCloud()
            width = (screenWidth / 960f) * cloudImage!!.width
            height = width * cloudImage!!.height / cloudImage!!.width
            val outset = screenWidth * OUTSET
            x = Utils.getRandomFloat(outset, screenWidth - outset - width)
        }

        isAlive = y <= screenHeight

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
        x,
        y,
        x + width,
        y + height
    )

    override fun onDispose() {

    }

    private fun getRandomCloud(): Image {
        val randomInt = Utils.getRandomInt(1,5)
        return resourceManager.clouds!![randomInt]!!
    }

}