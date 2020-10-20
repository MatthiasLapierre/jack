package com.matthiaslapierre.jack.core.game.impl.sprites.platforms

import android.graphics.*
import com.matthiaslapierre.jack.Constants.UNDEFINED
import com.matthiaslapierre.jack.core.resources.ResourceManager
import com.matthiaslapierre.framework.resources.Image
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jack.Constants.JUMPING_PLATFORM_BOUNCE_AREA_HEIGHT
import com.matthiaslapierre.jack.Constants.JUMPING_PLATFORM_BOUNCE_AREA_OUTSET
import com.matthiaslapierre.jack.Constants.JUMPING_PLATFORM_WIDTH
import com.matthiaslapierre.jack.Constants.SPRITE_LIFE_LOWEST_Y
import com.matthiaslapierre.jack.core.JumpPlatformState
import com.matthiaslapierre.jack.core.game.GameStates
import com.matthiaslapierre.jack.core.game.GameStates.Direction
import com.matthiaslapierre.jack.core.game.impl.sprites.player.PlayerSprite
import com.matthiaslapierre.jack.utils.Utils
import java.util.*

/**
 * Jumping platform.
 */
class JumpingPlatformSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates,
    override var x: Float,
    override var y: Float
): Sprite {

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
            width = screenWidth * JUMPING_PLATFORM_WIDTH
            height = width * firstFrame.height / firstFrame.width
        }

        isAlive = y <= (screenHeight * SPRITE_LIFE_LOWEST_Y)

        if (status == Sprite.Status.STATUS_PLAY) {
            y += gameStates.speedY
        }

        val firstFrame = platformImages!![state]!![frame]
        canvas.drawBitmap(
            firstFrame.bitmap,
            firstFrame.rect,
            getRectF(),
            globalPaint
        )

        if (status != Sprite.Status.STATUS_PAUSE) {
            if (state == JumpPlatformState.BOUNCE) {
                if (frame < 4) {
                    frame++
                } else {
                    state = JumpPlatformState.IDLE
                    frame = 0
                }
            }
        }
    }

    override fun isAlive(): Boolean = isAlive

    override fun isHit(sprite: Sprite): Boolean = sprite is PlayerSprite
            && gameStates.direction == Direction.DOWN
            && sprite.getFeetRectF().intersect(getBounceArea())

    override fun getScore(): Int = 0

    override fun getRectF(): RectF = RectF(
        x - (width / 2f),
        y - (height / 2f),
        x + (width / 2f),
        y + (height / 2f)
    )

    override fun onDispose() {

    }

    fun bounce() {
        state = JumpPlatformState.BOUNCE
    }

    private fun getBounceArea(): RectF = getRectF().run {
        RectF(
            left + (width * JUMPING_PLATFORM_BOUNCE_AREA_OUTSET),
            top,
            right - (width * JUMPING_PLATFORM_BOUNCE_AREA_OUTSET),
            top + (height * JUMPING_PLATFORM_BOUNCE_AREA_HEIGHT)
        )
    }

    private fun getRandomJumpingPlatform(): Hashtable<JumpPlatformState, Array<Image>> {
        val randomInt = Utils.getRandomInt(0,3)
        return resourceManager.jumpingPlatforms!![randomInt]
    }

}