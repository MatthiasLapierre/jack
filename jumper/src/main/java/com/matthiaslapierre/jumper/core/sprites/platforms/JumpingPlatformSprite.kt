package com.matthiaslapierre.jumper.core.sprites.platforms

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.JumpPlatformState
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.utils.Utils
import java.util.*

class JumpingPlatformSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float
): Sprite {

    companion object {
        private const val WIDTH_RATIO = .2f
    }

    private var platformImages: Hashtable<JumpPlatformState, Array<Image>>? = null
    private var state: JumpPlatformState = JumpPlatformState.IDLE
    private var frame: Int = 0
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var isAlive: Boolean = true

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        if (platformImages == null) {
            platformImages = getRandomJumpingPlatform()
            val firstFrame = platformImages!![JumpPlatformState.IDLE]!![0]
            width = screenWidth * WIDTH_RATIO
            height = width * firstFrame.height / firstFrame.width
        }

        isAlive = y <= (screenHeight * 2f)

        if (gameStates.currentStatus == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speed
        }

        val firstFrame = platformImages!![state]!![frame]
        val srcRect = Rect(
            0,
            0,
            firstFrame.width,
            firstFrame.height
        )
        val dstRect = getRectF()
        canvas.drawBitmap(
            firstFrame.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )

        if (state == JumpPlatformState.BOUNCE && frame == 4) {
            state = JumpPlatformState.IDLE
            frame = 0
        }
    }

    override fun isAlive(): Boolean = isAlive

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

    private fun getRandomJumpingPlatform(): Hashtable<JumpPlatformState, Array<Image>> {
        val randomInt = Utils.getRandomInt(0,3)
        return resourceManager.jumpingPlatforms!![randomInt]
    }

}