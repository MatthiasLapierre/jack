package com.matthiaslapierre.jumper.core.sprites.obstacles

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.core.sprites.player.PlayerSprite

class BatSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float,
    private var minX: Float,
    private var maxX: Float
) : Sprite {

    companion object {
        private const val WIDTH_RATIO = .27f
        private const val SPEED_RATIO = .02f
        private const val FRAME_PER_MS = 120
    }

    private var frame: Int = 0
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var speed: Float = UNDEFINED
    private var lastFrameTimestamp: Long = 0L
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val batImages = resourceManager.bat!!
        val batImage = batImages[frame]

        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()
        if (width == UNDEFINED) {
            width = screenWidth * WIDTH_RATIO
            height = width * batImage.height / batImage.width
            speed = width * SPEED_RATIO
        }

        isAlive = y <= (screenHeight * 2f)

        if(maxX - minX > width) {
            x += speed
            if (x < minX || x > maxX) {
                speed = -speed
            }
        }

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        val srcRect = Rect(
            0,
            0,
            batImage.width,
            batImage.height
        )
        val dstRect = getRectF()
        canvas.drawBitmap(
            batImage.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )

        if(System.currentTimeMillis() - lastFrameTimestamp > FRAME_PER_MS) {
            frame++
            if (frame >= batImages.size) {
                frame = 0
            }
            lastFrameTimestamp = System.currentTimeMillis()
        }
    }

    override fun isAlive(): Boolean = isAlive

    override fun isHit(sprite: Sprite): Boolean = sprite is PlayerSprite
            && sprite.getRectF().intersect(getRectF())

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