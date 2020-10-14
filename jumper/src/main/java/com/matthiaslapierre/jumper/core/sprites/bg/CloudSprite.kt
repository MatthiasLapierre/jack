package com.matthiaslapierre.jumper.core.sprites.bg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants.CLOUD_MAX_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.CLOUD_MIN_WIDTH
import com.matthiaslapierre.jumper.JumperConstants.CLOUD_OUTSET
import com.matthiaslapierre.jumper.JumperConstants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.utils.Utils

internal class CloudSprite(
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

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += speed
        }

        Log.d(">>>>>>>>>>> ", ">>>>>>>>>>>> y: $y")

        val srcRect = Rect(
            0,
            0,
            cloudImage.width,
            cloudImage.height
        )
        val dstRect = getRectF()

        canvas.drawBitmap(
            cloudImage.bitmap,
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