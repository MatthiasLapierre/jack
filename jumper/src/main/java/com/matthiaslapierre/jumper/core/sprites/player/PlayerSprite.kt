package com.matthiaslapierre.jumper.core.sprites.player

import android.graphics.*
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_FEET_BOTTOM
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_FEET_TOP
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_INITIAL_POSITION
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_INSET_X
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_INSET_Y
import com.matthiaslapierre.jumper.JumperConstants.ROCKET_TOP
import com.matthiaslapierre.jumper.core.GameStates
import com.matthiaslapierre.jumper.utils.hasFlag

internal class PlayerSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
): Sprite {

    override var x: Float = UNDEFINED
    override var y: Float = UNDEFINED

    var highestY: Float = UNDEFINED
    var lowestY: Float = UNDEFINED

    private var frame: Int = 0
    private var rocketFrame: Int = 0
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED
    private var previousState: PlayerState = PlayerState.IDLE

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val images = resourceManager.player!![gameStates.playerState]!!
        if (frame > images.size - 1) {
            frame = 0
        }
        val image = images[frame]!!

        val screenWidth = canvas.width.toFloat()
        screenHeight = canvas.height.toFloat()
        if (x == UNDEFINED) {
            width = screenWidth * JumperConstants.PLAYER_WIDTH
            height = width * image.height / image.width.toFloat()
            x = (screenWidth - width) / 2f
            y = screenHeight - (screenWidth * PLAYER_INITIAL_POSITION) - height
            lowestY = y
            highestY = (screenHeight - height) * JumperConstants.PLAYER_HIGHEST_Y
        }

        if (status == Sprite.Status.STATUS_PLAY
            || (status == Sprite.Status.STATUS_GAME_OVER && y < screenHeight)) {
            x -= gameStates.playerSpeedX
            y -= gameStates.playerSpeedY
            if (x > screenWidth) {
                x = -width
            } else if(x < -width) {
                x = screenWidth
            }
            if (y < highestY) {
                y = highestY
            } else if (status != Sprite.Status.STATUS_GAME_OVER && y > lowestY) {
                y = lowestY
            }
        }

        // Draw the rocket power-up
        if (gameStates.powerUp.hasFlag(GameStates.POWER_UP_ROCKET)) {
            val rocketImages = resourceManager.rocket!!
            val rocketImage = rocketImages[rocketFrame]
            val rocketWidth = width
            val rocketHeight = rocketWidth * rocketImage.height / rocketImage.width
            canvas.drawBitmap(
                rocketImage.bitmap,
                Rect(
                    0,
                    0,
                    rocketImage.width,
                    rocketImage.height
                ),
                RectF(
                    (x + width / 2f) - (rocketWidth / 2f),
                    y - (rocketHeight * ROCKET_TOP),
                    (x + width / 2f) + (rocketWidth / 2f),
                    y - (rocketHeight * ROCKET_TOP) + rocketHeight
                ),
                globalPaint
            )
            if (rocketFrame < rocketImages.size - 2) {
                rocketFrame++
            } else {
                rocketFrame = 0
            }
        }

        // Draw the main character.
        val dstRect = getRectF()
        canvas.drawBitmap(
            image.bitmap,
            Rect(
                0,
                0,
                image.width,
                image.height
            ),
            dstRect,
            globalPaint
        )

        // Draw power-ups
        if (gameStates.powerUp.hasFlag(GameStates.POWER_UP_MAGNET)) {
            val magnetImage = resourceManager.magnet!!
            val magnetWidth = width
            val magnetHeight = magnetWidth * magnetImage.height / magnetImage.width
            canvas.drawBitmap(
                magnetImage.bitmap,
                Rect(
                    0,
                    0,
                    magnetImage.width,
                    magnetImage.height
                ),
                RectF(
                    (x + width / 2f) - (magnetWidth / 2f),
                    y,
                    (x + width / 2f) + (magnetWidth / 2f),
                    y + magnetHeight
                ),
                globalPaint
            )
        } else if (gameStates.powerUp.hasFlag(GameStates.POWER_UP_ARMORED)) {
            val armorImage = resourceManager.armor!!
            val armorWidth = width
            val armorHeight = armorWidth * armorImage.height / armorImage.width
            canvas.drawBitmap(
                armorImage.bitmap,
                Rect(
                    0,
                    0,
                    armorImage.width,
                    armorImage.height
                ),
                RectF(
                    (x + width / 2f) - (armorWidth / 2f),
                    y,
                    (x + width / 2f) + (armorWidth / 2f),
                    y + armorHeight
                ),
                globalPaint
            )
        }

        frame = getNextFrameIndex(frame, previousState, gameStates.playerState)
        previousState = gameStates.playerState

        //DEBUG
        /*canvas.drawRect(RectF(0f, highestY, screenWidth, highestY+1), Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        })*/
        canvas.drawRect(RectF(0f, lowestY, screenWidth, lowestY+1), Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLUE
        })
        canvas.drawRect(getFeetRectF(), Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        })
    }

    override fun isAlive(): Boolean = true

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

    fun getFeetRectF(): RectF = getRectF().run {
        RectF(
            left + (width * PLAYER_INSET_X),
            bottom - (height * PLAYER_FEET_TOP),
            right - (width * PLAYER_INSET_X),
            bottom - (height * PLAYER_FEET_BOTTOM)
        )
    }

    fun getBodyRectF(): RectF = getRectF().run {
        RectF(
            left + (width * PLAYER_INSET_X),
            top + (height * PLAYER_INSET_Y),
            right - (width * PLAYER_INSET_X),
            bottom - (height * PLAYER_INSET_Y)
        )
    }

    private fun getNextFrameIndex(previousFrameIndex: Int, previousState: PlayerState, state: PlayerState): Int {
        val playerState = gameStates.playerState
        val images = resourceManager.player!![playerState]!!
        var frame = previousFrameIndex
        if(previousState == state) {
            when (playerState) {
                PlayerState.JUMP, PlayerState.FALL -> {
                    if (frame < images.size - 1) {
                        frame++
                    }
                }
                PlayerState.COPTER, PlayerState.DEAD -> {
                    if (frame < images.size - 1) {
                        frame++
                    } else {
                        frame = 0
                    }
                }
                else -> frame = 0
            }
        } else {
            frame = 0
        }
        return frame
    }

}