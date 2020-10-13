package com.matthiaslapierre.jumper.core.sprites.player

import android.graphics.*
import android.util.Log
import com.matthiaslapierre.core.Constants.UNDEFINED
import com.matthiaslapierre.core.ResourceManager
import com.matthiaslapierre.core.ResourceManager.PlayerState
import com.matthiaslapierre.framework.ui.Sprite
import com.matthiaslapierre.jumper.JumperConstants
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_FRAME_RATE
import com.matthiaslapierre.jumper.JumperConstants.PLAYER_INITIAL_POSITION
import com.matthiaslapierre.jumper.core.GameStates

class PlayerSprite(
    private val resourceManager: ResourceManager,
    private val gameStates: GameStates
): Sprite {

    override var x: Float = UNDEFINED
    override var y: Float = UNDEFINED

    private var frame: Int = 0
    private var highestY: Float = UNDEFINED
    private var lowestY: Float = UNDEFINED
    private var width: Float = UNDEFINED
    private var height: Float = UNDEFINED
    private var screenHeight: Float = UNDEFINED
    private var lastFrameTimestamp: Long = 0L
    private var previousState: PlayerState = PlayerState.IDLE

    override fun onDraw(canvas: Canvas, globalPaint: Paint, status: Sprite.Status) {
        val images = resourceManager.player!![gameStates.playerState]!!
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
            || status == Sprite.Status.STATUS_GAME_OVER) {
            x -= gameStates.speedX
            y -= gameStates.speedY
            if (x > screenWidth) {
                x = -width
            } else if(x < -width) {
                x = screenWidth
            }
            if (y < highestY) {
                y = highestY
            } else if (y > lowestY) {
                y = lowestY
            }
        }

        val srcRect = Rect(
            0,
            0,
            image.bitmap.width,
            image.bitmap.height
        )
        val dstRect = getRectF()

        canvas.drawBitmap(
            image.bitmap,
            srcRect,
            dstRect,
            globalPaint
        )

        frame = getFrameIndex(frame, previousState, gameStates.playerState)
        previousState = gameStates.playerState

        //DEBUG
        /*canvas.drawRect(getFeetRectF(), Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        })*/
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
            left + (width * .3f),
            bottom - (height * .2f),
            right - (width * .3f),
            bottom - (height * .1f)
        )
    }

    fun getFrameIndex(previousFrameIndex: Int, previousState: PlayerState, state: PlayerState): Int {
        val playerState = gameStates.playerState
        val images = resourceManager.player!![playerState]!!
        var frame = previousFrameIndex
        if(previousState == state) {
            if(System.currentTimeMillis() - lastFrameTimestamp > PLAYER_FRAME_RATE) {
                when (playerState) {
                    PlayerState.JUMP -> {
                        if (frame < images.size - 1) {
                            frame++
                        }
                    }
                    PlayerState.FALL -> {
                        if (frame < images.size - 1) {
                            frame++
                        }
                    }
                    else -> frame = 0
                }
                lastFrameTimestamp = System.currentTimeMillis()
            }
        } else {
            Log.d(">>>>>>>>>> ", ">>>>>>>> $previousState - $state")
            frame = 0
        }
        Log.d(">>>>>>>>>> ", ">>>>>>>> $playerState - $frame")
        return frame
    }

}